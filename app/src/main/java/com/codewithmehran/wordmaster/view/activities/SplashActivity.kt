package com.codewithmehran.wordmaster.view.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codewithmehran.wordmaster.databinding.ActivitySplashBinding
import com.codewithmehran.wordmaster.model.WordMasterApp

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private var hasShownAd = false
    private val sharedPreferences by lazy { getSharedPreferences("settings", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Restore state to prevent showing ad again on rotation
        hasShownAd = savedInstanceState?.getBoolean("hasShownAd", false) ?: false

        if (!hasShownAd) {
            loadAndShowAppOpenAd()
        } else {
            // Already shown ad, just navigate
            navigateToNextScreen()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("hasShownAd", hasShownAd)
    }

    private fun loadAndShowAppOpenAd() {
        val app = application as WordMasterApp
        
        // Load the ad
        app.loadAppOpenAd()
        
        // Give it a moment to load, then show
        binding.root.postDelayed({
            app.showAppOpenAd(this) {
                // Ad dismissed or failed, navigate to next screen
                hasShownAd = true
                navigateToNextScreen()
            }
        }, 1000) // 1 second delay to allow ad to load
    }

    private fun navigateToNextScreen() {
        val isFirstLaunch = sharedPreferences.getBoolean("is_first_launch", true)
        
        val intent = if (isFirstLaunch) {
            // First time user - go to IntroActivity
            Intent(this, IntroActivity::class.java)
        } else {
            // Returning user - go to MainActivity
            Intent(this, MainActivity::class.java)
        }
        
        startActivity(intent)
        finish()
    }
}
