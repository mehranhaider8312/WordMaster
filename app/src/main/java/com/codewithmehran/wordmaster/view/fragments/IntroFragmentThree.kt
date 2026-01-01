package com.codewithmehran.wordmaster.view.fragments

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.codewithmehran.wordmaster.R

class IntroFragmentThree : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_intro_three, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startAnimations(view)
    }

    private fun startAnimations(view: View) {
        val txtTitle = view.findViewById<TextView>(R.id.txtTitle)
        val txtDescription = view.findViewById<TextView>(R.id.txtDescription)
        val cardLanguages = view.findViewById<CardView>(R.id.cardLanguages)
        val txtGlobeEmoji = view.findViewById<TextView>(R.id.txtGlobeEmoji)
        val languagesGrid = view.findViewById<LinearLayout>(R.id.languagesGrid)
        val langEnglish = view.findViewById<LinearLayout>(R.id.langEnglish)
        val langUrdu = view.findViewById<LinearLayout>(R.id.langUrdu)
        val langArabic = view.findViewById<LinearLayout>(R.id.langArabic)
        val langMore = view.findViewById<LinearLayout>(R.id.langMore)
        val featuresContainer = view.findViewById<LinearLayout>(R.id.featuresContainer)
        val feature1 = view.findViewById<LinearLayout>(R.id.feature1)
        val feature2 = view.findViewById<LinearLayout>(R.id.feature2)
        val feature3 = view.findViewById<LinearLayout>(R.id.feature3)
        val decorGlobe1 = view.findViewById<View>(R.id.decorGlobe1)
        val decorGlobe2 = view.findViewById<View>(R.id.decorGlobe2)

        animateGlobes(decorGlobe1, decorGlobe2)

        // Title animation
        val titleAlpha = ObjectAnimator.ofFloat(txtTitle, "alpha", 0f, 1f).apply {
            duration = 600
        }
        val titleTransY = ObjectAnimator.ofFloat(txtTitle, "translationY", 50f, 0f).apply {
            duration = 600
            interpolator = DecelerateInterpolator()
        }
        AnimatorSet().apply {
            playTogether(titleAlpha, titleTransY)
            startDelay = 200
            start()
        }

        // Description animation
        val descAlpha = ObjectAnimator.ofFloat(txtDescription, "alpha", 0f, 1f).apply {
            duration = 600
        }
        val descTransY = ObjectAnimator.ofFloat(txtDescription, "translationY", 40f, 0f).apply {
            duration = 600
            interpolator = DecelerateInterpolator()
        }
        AnimatorSet().apply {
            playTogether(descAlpha, descTransY)
            startDelay = 600
            start()
        }

        // Card animation
        val cardAlpha = ObjectAnimator.ofFloat(cardLanguages, "alpha", 0f, 1f).apply {
            duration = 600
        }
        val cardScaleX = ObjectAnimator.ofFloat(cardLanguages, "scaleX", 0.8f, 1.05f, 1f).apply {
            duration = 800
            interpolator = BounceInterpolator()
        }
        val cardScaleY = ObjectAnimator.ofFloat(cardLanguages, "scaleY", 0.8f, 1.05f, 1f).apply {
            duration = 800
            interpolator = BounceInterpolator()
        }
        AnimatorSet().apply {
            playTogether(cardAlpha, cardScaleX, cardScaleY)
            startDelay = 1000
            start()
        }

        // Globe emoji animation - scale and rotate
        ObjectAnimator.ofFloat(txtGlobeEmoji, "scaleX", 0f, 1.3f, 1f).apply {
            duration = 800
            startDelay = 1400
            interpolator = OvershootInterpolator(2f)
            start()
        }
        ObjectAnimator.ofFloat(txtGlobeEmoji, "scaleY", 0f, 1.3f, 1f).apply {
            duration = 800
            startDelay = 1400
            interpolator = OvershootInterpolator(2f)
            start()
        }
        ObjectAnimator.ofFloat(txtGlobeEmoji, "rotation", 0f, 360f).apply {
            duration = 800
            startDelay = 1400
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }

        // Continuous globe rotation
        view.postDelayed({
            ObjectAnimator.ofFloat(txtGlobeEmoji, "rotation", 0f, 360f).apply {
                duration = 4000
                repeatCount = ObjectAnimator.INFINITE
                interpolator = AccelerateDecelerateInterpolator()
                start()
            }
        }, 2200)

        // Languages grid fade in
        ObjectAnimator.ofFloat(languagesGrid, "alpha", 0f, 1f).apply {
            duration = 600
            startDelay = 2200
            start()
        }

        // Animate language cards
        animateLanguageCard(langEnglish, 2400)
        animateLanguageCard(langUrdu, 2550)
        animateLanguageCard(langArabic, 2700)
        animateLanguageCard(langMore, 2850)

        // Features container
        ObjectAnimator.ofFloat(featuresContainer, "alpha", 0f, 1f).apply {
            duration = 400
            startDelay = 3200
            start()
        }

        // Feature cards
        animateFeatureCard(feature1, 3400)
        animateFeatureCard(feature2, 3600)
        animateFeatureCard(feature3, 3800)
    }

    private fun animateLanguageCard(card: LinearLayout, delay: Long) {
        val scaleX = ObjectAnimator.ofFloat(card, "scaleX", 0f, 1.15f, 1f).apply {
            duration = 500
            interpolator = OvershootInterpolator(1.5f)
        }
        val scaleY = ObjectAnimator.ofFloat(card, "scaleY", 0f, 1.15f, 1f).apply {
            duration = 500
            interpolator = OvershootInterpolator(1.5f)
        }
        val alpha = ObjectAnimator.ofFloat(card, "alpha", 0f, 1f).apply {
            duration = 400
        }
        val rotation = ObjectAnimator.ofFloat(card, "rotation", -10f, 10f, 0f).apply {
            duration = 500
            interpolator = AccelerateDecelerateInterpolator()
        }

        card.scaleX = 0f
        card.scaleY = 0f
        card.alpha = 0f

        AnimatorSet().apply {
            playTogether(scaleX, scaleY, alpha, rotation)
            startDelay = delay
            start()
        }

        // Add a subtle hover effect after initial animation
        view?.postDelayed({
            val hoverScaleX = ObjectAnimator.ofFloat(card, "scaleX", 1f, 1.05f, 1f).apply {
                duration = 1500
                repeatCount = ObjectAnimator.INFINITE
                interpolator = AccelerateDecelerateInterpolator()
            }
            val hoverScaleY = ObjectAnimator.ofFloat(card, "scaleY", 1f, 1.05f, 1f).apply {
                duration = 1500
                repeatCount = ObjectAnimator.INFINITE
                interpolator = AccelerateDecelerateInterpolator()
            }
            AnimatorSet().apply {
                playTogether(hoverScaleX, hoverScaleY)
                start()
            }
        }, delay + 600)
    }

    private fun animateFeatureCard(card: LinearLayout, delay: Long) {
        val alpha = ObjectAnimator.ofFloat(card, "alpha", 0f, 1f).apply {
            duration = 500
        }
        val transX = ObjectAnimator.ofFloat(card, "translationX", -100f, 0f).apply {
            duration = 500
            interpolator = OvershootInterpolator()
        }
        val scaleX = ObjectAnimator.ofFloat(card, "scaleX", 0.8f, 1f).apply {
            duration = 500
        }
        val scaleY = ObjectAnimator.ofFloat(card, "scaleY", 0.8f, 1f).apply {
            duration = 500
        }

        card.alpha = 0f

        AnimatorSet().apply {
            playTogether(alpha, transX, scaleX, scaleY)
            startDelay = delay
            start()
        }
    }

    private fun animateGlobes(globe1: View, globe2: View) {
        // Globe 1 - floating and rotating
        val globe1Rotation = ObjectAnimator.ofFloat(globe1, "rotation", 0f, 360f).apply {
            duration = 5000
            repeatCount = ObjectAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }
        val globe1TransY = ObjectAnimator.ofFloat(globe1, "translationY", 0f, 25f, 0f).apply {
            duration = 3500
            repeatCount = ObjectAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }
        val globe1TransX = ObjectAnimator.ofFloat(globe1, "translationX", 0f, -15f, 0f).apply {
            duration = 3500
            repeatCount = ObjectAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }
        val globe1Scale = ObjectAnimator.ofFloat(globe1, "scaleX", 1f, 1.15f, 1f).apply {
            duration = 3000
            repeatCount = ObjectAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }
        val globe1ScaleY = ObjectAnimator.ofFloat(globe1, "scaleY", 1f, 1.15f, 1f).apply {
            duration = 3000
            repeatCount = ObjectAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }
        AnimatorSet().apply {
            playTogether(globe1Rotation, globe1TransY, globe1TransX, globe1Scale, globe1ScaleY)
            start()
        }

        // Globe 2 - opposite rotation and movement
        val globe2Rotation = ObjectAnimator.ofFloat(globe2, "rotation", 0f, -360f).apply {
            duration = 4500
            repeatCount = ObjectAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }
        val globe2TransY = ObjectAnimator.ofFloat(globe2, "translationY", 0f, -20f, 0f).apply {
            duration = 3000
            repeatCount = ObjectAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }
        val globe2TransX = ObjectAnimator.ofFloat(globe2, "translationX", 0f, 18f, 0f).apply {
            duration = 3000
            repeatCount = ObjectAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }
        val globe2Scale = ObjectAnimator.ofFloat(globe2, "scaleX", 1f, 1.2f, 1f).apply {
            duration = 3200
            repeatCount = ObjectAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }
        val globe2ScaleY = ObjectAnimator.ofFloat(globe2, "scaleY", 1f, 1.2f, 1f).apply {
            duration = 3200
            repeatCount = ObjectAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }
        AnimatorSet().apply {
            playTogether(globe2Rotation, globe2TransY, globe2TransX, globe2Scale, globe2ScaleY)
            start()
        }
    }
}