package com.codewithmehran.wordmaster.model

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.codewithmehran.wordmaster.data.dao.QuizDao
import com.codewithmehran.wordmaster.data.dao.StreakDao
import com.codewithmehran.wordmaster.data.dao.WordDao
import com.codewithmehran.wordmaster.data.repository.QuizRepository
import com.codewithmehran.wordmaster.data.repository.StreakRepository
import com.codewithmehran.wordmaster.data.repository.WordRepository
import com.codewithmehran.wordmaster.work.DailyReminderWorker
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import java.util.concurrent.TimeUnit

class WordMasterApp : Application() {

    companion object {
        private const val TAG = "WordMasterApp"
        
        private const val APP_OPEN_AD_UNIT_ID = "ca-app-pub-3940256099942544/9257395921"
        private const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
        private const val BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
    }

    val wordRepository: WordRepository by lazy {
        WordRepository(WordDao(this))
    }

    val streakRepository: StreakRepository by lazy {
        StreakRepository(StreakDao(this))
    }

    val quizRepository: QuizRepository by lazy {
        QuizRepository(QuizDao(this))
    }

    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAppOpenAd = false
    private var interstitialAd: InterstitialAd? = null
    private var isLoadingInterstitialAd = false

    override fun onCreate() {
        super.onCreate()
        applyTheme()
        scheduleDailyReminder()
        initializeAdMob()
    }

    private fun applyTheme() {
        val sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("is_dark_mode", false)
        val mode = if (isDarkMode) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    private fun scheduleDailyReminder() {
        val workRequest =
            PeriodicWorkRequestBuilder<DailyReminderWorker>(24, TimeUnit.HOURS)
                .build()
        WorkManager.Companion.getInstance(this).enqueueUniquePeriodicWork(
            "daily_reminder",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }


    private fun initializeAdMob() {
        MobileAds.initialize(this) { initializationStatus ->
            Log.d(TAG, "AdMob initialized: ${initializationStatus.adapterStatusMap}")
            // Preload ads after initialization
            loadInterstitialAd()
        }
    }


    fun loadAppOpenAd() {
        if (isLoadingAppOpenAd || appOpenAd != null) {
            return
        }

        isLoadingAppOpenAd = true
        val request = AdRequest.Builder().build()

        AppOpenAd.load(
            this,
            APP_OPEN_AD_UNIT_ID,
            request,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    isLoadingAppOpenAd = false
                    Log.d(TAG, "App Open Ad loaded successfully")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    isLoadingAppOpenAd = false
                    Log.e(TAG, "App Open Ad failed to load: ${loadAdError.message}")
                }
            }
        )
    }

    fun showAppOpenAd(activity: Activity, onAdDismissed: () -> Unit) {
        if (appOpenAd == null) {
            Log.d(TAG, "App Open Ad not ready, proceeding without ad")
            onAdDismissed()
            return
        }

        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                appOpenAd = null
                Log.d(TAG, "App Open Ad dismissed")
                onAdDismissed()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                appOpenAd = null
                Log.e(TAG, "App Open Ad failed to show: ${adError.message}")
                onAdDismissed()
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "App Open Ad showed")
            }
        }

        appOpenAd?.show(activity)
    }


    fun loadInterstitialAd() {
        if (isLoadingInterstitialAd || interstitialAd != null) {
            return
        }

        isLoadingInterstitialAd = true
        val request = AdRequest.Builder().build()

        InterstitialAd.load(
            this,
            INTERSTITIAL_AD_UNIT_ID,
            request,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isLoadingInterstitialAd = false
                    Log.d(TAG, "Interstitial Ad loaded successfully")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    interstitialAd = null
                    isLoadingInterstitialAd = false
                    Log.e(TAG, "Interstitial Ad failed to load: ${loadAdError.message}")
                }
            }
        )
    }

    fun showInterstitialAdIfReady(activity: Activity) {
        if (interstitialAd == null) {
            Log.d(TAG, "Interstitial Ad not ready")
            loadInterstitialAd() // Try to load for next time
            return
        }

        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
                Log.d(TAG, "Interstitial Ad dismissed")
                loadInterstitialAd() // Reload for next time
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                interstitialAd = null
                Log.e(TAG, "Interstitial Ad failed to show: ${adError.message}")
                loadInterstitialAd() // Reload for next time
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "Interstitial Ad showed")
            }
        }

        interstitialAd?.show(activity)
    }


    fun createBannerAdView(context: Context, adSize: AdSize = AdSize.BANNER): AdView {
        val adView = AdView(context)
        adView.adUnitId = BANNER_AD_UNIT_ID
        adView.setAdSize(adSize)
        
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        
        return adView
    }
}
