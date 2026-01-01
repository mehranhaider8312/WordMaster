package com.codewithmehran.wordmaster.view.activities

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import com.codewithmehran.wordmaster.databinding.ActivitySplashBinding
import com.codewithmehran.wordmaster.model.WordMasterApp

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private var hasShownAd = false
    private val sharedPreferences by lazy { getSharedPreferences("settings", MODE_PRIVATE) }
    private var animationsComplete = false
    private var adLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hasShownAd = savedInstanceState?.getBoolean("hasShownAd", false) ?: false
        if (!hasShownAd) {
            loadAppOpenAd()
        }
        startSplashAnimations()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("hasShownAd", hasShownAd)
    }

    private fun startSplashAnimations() {
        animateFloatingWords()
        val logoScaleX = ObjectAnimator.ofFloat(binding.imgLogo, "scaleX", 0f, 1.2f, 1f).apply {
            duration = 800
            interpolator = OvershootInterpolator()
        }
        val logoScaleY = ObjectAnimator.ofFloat(binding.imgLogo, "scaleY", 0f, 1.2f, 1f).apply {
            duration = 800
            interpolator = OvershootInterpolator()
        }
        val logoRotation = ObjectAnimator.ofFloat(binding.imgLogo, "rotation", 0f, 360f).apply {
            duration = 800
            interpolator = AccelerateDecelerateInterpolator()
        }

        AnimatorSet().apply {
            playTogether(logoScaleX, logoScaleY, logoRotation)
            startDelay = 200
            start()
        }
        ObjectAnimator.ofFloat(binding.txtAppName, "alpha", 0f, 1f).apply {
            duration = 600
            startDelay = 800
            start()
        }
        ObjectAnimator.ofFloat(binding.txtAppName, "translationY", 30f, 0f).apply {
            duration = 600
            startDelay = 800
            interpolator = DecelerateInterpolator()
            start()
        }
        ObjectAnimator.ofFloat(binding.txtTagline, "alpha", 0f, 1f).apply {
            duration = 500
            startDelay = 1200
            start()
        }

        val cardAlpha = ObjectAnimator.ofFloat(binding.cardDefinition, "alpha", 0f, 1f).apply {
            duration = 600
        }
        val cardTranslation = ObjectAnimator.ofFloat(binding.cardDefinition, "translationY", 50f, 0f).apply {
            duration = 600
            interpolator = DecelerateInterpolator()
        }
        AnimatorSet().apply {
            playTogether(cardAlpha, cardTranslation)
            startDelay = 1600
            start()
        }

        binding.root.postDelayed({ updateWord("Serendipity", "Finding good things by chance") }, 2500)

        ObjectAnimator.ofFloat(binding.progressBar, "alpha", 0f, 1f).apply {
            duration = 400
            startDelay = 2200
            doOnEnd {
                animationsComplete = true
                checkAndProceed()
            }
            start()
        }
    }

    private fun checkAndProceed() {
        if (animationsComplete) {
            if (!hasShownAd) {
                showAppOpenAdWhenReady()
            } else {
                navigateToNextScreen()
            }
        }
    }

    private fun loadAppOpenAd() {
        val app = application as WordMasterApp
        app.loadAppOpenAd()
        adLoaded = true
    }

    private fun showAppOpenAdWhenReady() {
        val app = application as WordMasterApp
        binding.root.postDelayed({
            app.showAppOpenAd(this) {
                hasShownAd = true
                navigateToNextScreen()
            }
        }, 300)
    }

    private fun animateFloatingWords() {
        val float1Alpha = ObjectAnimator.ofFloat(binding.txtFloatingWord1, "alpha", 0f, 0.3f, 0f).apply {
            duration = 3000
            repeatCount = ObjectAnimator.INFINITE
        }
        val float1TransY = ObjectAnimator.ofFloat(binding.txtFloatingWord1, "translationY", 0f, -30f, 0f).apply {
            duration = 3000
            repeatCount = ObjectAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }
        AnimatorSet().apply {
            playTogether(float1Alpha, float1TransY)
            startDelay = 300
            start()
        }

        val float2Alpha = ObjectAnimator.ofFloat(binding.txtFloatingWord2, "alpha", 0f, 0.3f, 0f).apply {
            duration = 3500
            repeatCount = ObjectAnimator.INFINITE
        }
        val float2TransY = ObjectAnimator.ofFloat(binding.txtFloatingWord2, "translationY", 0f, 30f, 0f).apply {
            duration = 3500
            repeatCount = ObjectAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }
        AnimatorSet().apply {
            playTogether(float2Alpha, float2TransY)
            startDelay = 600
            start()
        }

        val float3Alpha = ObjectAnimator.ofFloat(binding.txtFloatingWord3, "alpha", 0f, 0.3f, 0f).apply {
            duration = 4000
            repeatCount = ObjectAnimator.INFINITE
        }
        val float3TransY = ObjectAnimator.ofFloat(binding.txtFloatingWord3, "translationY", 0f, -25f, 0f).apply {
            duration = 4000
            repeatCount = ObjectAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }
        AnimatorSet().apply {
            playTogether(float3Alpha, float3TransY)
            startDelay = 900
            start()
        }

        val float4Alpha = ObjectAnimator.ofFloat(binding.txtFloatingWord4, "alpha", 0f, 0.3f, 0f).apply {
            duration = 3200
            repeatCount = ObjectAnimator.INFINITE
        }
        val float4TransY = ObjectAnimator.ofFloat(binding.txtFloatingWord4, "translationY", 0f, 35f, 0f).apply {
            duration = 3200
            repeatCount = ObjectAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }
        AnimatorSet().apply {
            playTogether(float4Alpha, float4TransY)
            startDelay = 1200
            start()
        }
    }

    private fun updateWord(word: String, definition: String) {
        ObjectAnimator.ofFloat(binding.cardDefinition, "alpha", 1f, 0f).apply {
            duration = 200
            doOnEnd {
                binding.txtWord.text = word
                binding.txtDefinition.text = definition

                ObjectAnimator.ofFloat(binding.cardDefinition, "alpha", 0f, 1f).apply {
                    duration = 200
                    start()
                }
            }
            start()
        }
    }

//    private fun loadAndShowAppOpenAd() {
//        val app = application as WordMasterApp
//
//        app.loadAppOpenAd()
//        binding.root.postDelayed({
//            app.showAppOpenAd(this) {
//                hasShownAd = true
//                navigateToNextScreen()
//            }
//        }, 3000) // 3 seconds for animations
//    }

    private fun navigateToNextScreen() {
        val isFirstLaunch = sharedPreferences.getBoolean("is_first_launch", true)

        val intent = if (isFirstLaunch) {
            Intent(this, IntroActivity::class.java)
        } else {
            Intent(this, MainActivity::class.java)
        }

        startActivity(intent)
        finish()
    }
}