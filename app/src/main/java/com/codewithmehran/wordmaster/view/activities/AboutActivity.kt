package com.codewithmehran.wordmaster.view.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.codewithmehran.wordmaster.databinding.ActivityAboutBinding
import com.codewithmehran.wordmaster.model.WordMasterApp
import com.google.android.gms.ads.AdSize

class AboutActivity : BaseAdActivity() {

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener {
            trackClick() // Track click for interstitial ad
            finish()
        }
        setupBannerAd()
    }

    private fun setupBannerAd() {
        val app = application as WordMasterApp
        val bannerAd = app.createBannerAdView(this, AdSize.MEDIUM_RECTANGLE)
        binding.bannerAdContainer.removeAllViews()
        binding.bannerAdContainer.addView(bannerAd)
    }
}
