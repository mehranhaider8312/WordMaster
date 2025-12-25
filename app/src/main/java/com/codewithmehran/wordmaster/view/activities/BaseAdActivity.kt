package com.codewithmehran.wordmaster.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codewithmehran.wordmaster.model.WordMasterApp

/**
 * Base activity that provides click tracking for interstitial ads.
 * Shows an interstitial ad after every 3 user clicks.
 */
abstract class BaseAdActivity : AppCompatActivity() {

    companion object {
        private const val CLICKS_BEFORE_AD = 3
        private const val KEY_CLICK_COUNTER = "click_counter"
    }

    private var clickCounter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Restore click counter across configuration changes
        clickCounter = savedInstanceState?.getInt(KEY_CLICK_COUNTER, 0) ?: 0
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_CLICK_COUNTER, clickCounter)
    }

    /**
     * Call this method on user interactions (button clicks, etc.)
     * to track clicks and show interstitial ad after 3 clicks.
     */
    protected fun trackClick() {
        clickCounter++
        
        if (clickCounter >= CLICKS_BEFORE_AD) {
            // Reset counter and show ad
            clickCounter = 0
            showInterstitialAd()
        }
    }

    private fun showInterstitialAd() {
        val app = application as WordMasterApp
        app.showInterstitialAdIfReady(this)
    }
}
