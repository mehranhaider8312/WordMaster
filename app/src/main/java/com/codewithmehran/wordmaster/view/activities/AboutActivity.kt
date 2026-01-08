package com.codewithmehran.wordmaster.view.activities

import android.os.Bundle
import android.view.animation.AnimationUtils
import android.view.animation.BounceInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.core.view.doOnPreDraw
import com.codewithmehran.wordmaster.R
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
            trackClick()
            finish()
        }

        setupBannerAd()
        setupAnimations()
    }

    private fun setupBannerAd() {
        val app = application as WordMasterApp
        val bannerAd = app.createBannerAdView(this, AdSize.MEDIUM_RECTANGLE)
        binding.bannerAdContainer.removeAllViews()
        binding.bannerAdContainer.addView(bannerAd)
    }

    private fun setupAnimations() {
        // Wait for layout to be drawn before starting animations
        binding.root.doOnPreDraw {
            startStaggeredAnimations()
        }
    }

    private fun startStaggeredAnimations() {
        // Logo scale animation
        binding.logoCard.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(800)
            .setInterpolator(BounceInterpolator())
            .withEndAction {
                // After logo animation, start other animations
                animateAppName()
            }
            .start()

        // Version badge animation (delayed)
        binding.txtVersion.postDelayed({
            binding.txtVersion.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(600)
                .start()
        }, 300)
    }

    private fun animateAppName() {
        binding.txtAppName.animate()
            .alpha(1f)
            .setDuration(600)
            .withEndAction {
                // After app name, animate description card
                animateDescriptionCard()
            }
            .start()
    }

    private fun animateDescriptionCard() {
        binding.descCard.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(700)
            .withEndAction {
                // After description, animate features title
                animateFeaturesTitle()
            }
            .start()
    }

    private fun animateFeaturesTitle() {
        binding.txtFeaturesTitle.animate()
            .alpha(1f)
            .setDuration(500)
            .withEndAction {
                // Staggered animation for features
                animateFeaturesStaggered()
            }
            .start()
    }

    private fun animateFeaturesStaggered() {
        val features = listOf(
            binding.feature1,
            binding.feature2,
            binding.feature3,
            binding.feature4
        )

        features.forEachIndexed { index, feature ->
            feature.postDelayed({
                feature.animate()
                    .translationX(0f)
                    .alpha(1f)
                    .setDuration(600)
                    .setInterpolator(AnimationUtils.loadInterpolator(this, android.R.interpolator.overshoot))
                    .withEndAction {
                        // Animate developer card after last feature
                        if (index == features.lastIndex) {
                            animateDeveloperCard()
                        }
                    }
                    .start()
            }, index * 150L) // 150ms delay between each feature
        }
    }

    private fun animateDeveloperCard() {
        binding.devCard.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(700)
            .setInterpolator(AnimationUtils.loadInterpolator(this, android.R.interpolator.decelerate_quad))
            .start()
    }

    override fun onResume() {
        super.onResume()
        // Restart logo animation when activity resumes
        binding.logoCard.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(300)
            .withEndAction {
                binding.logoCard.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(300)
                    .start()
            }
            .start()
    }
}