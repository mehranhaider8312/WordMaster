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
import androidx.fragment.app.Fragment
import com.codewithmehran.wordmaster.databinding.FragmentIntroOneBinding

class IntroFragmentOne : Fragment() {

    private var _binding: FragmentIntroOneBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIntroOneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startAnimations()
    }

    private fun startAnimations() {
        animateDecorativeCircles(binding.decorCircle1, binding.decorCircle2)

        val cardScaleX = ObjectAnimator.ofFloat(binding.cardImage, "scaleX", 0f, 1.1f, 1f).apply {
            duration = 800
            interpolator = BounceInterpolator()
        }
        val cardScaleY = ObjectAnimator.ofFloat(binding.cardImage, "scaleY", 0f, 1.1f, 1f).apply {
            duration = 800
            interpolator = BounceInterpolator()
        }
        val cardRotation = ObjectAnimator.ofFloat(binding.cardImage, "rotation", -10f, 10f, 0f).apply {
            duration = 800
            interpolator = AccelerateDecelerateInterpolator()
        }
        AnimatorSet().apply {
            playTogether(cardScaleX, cardScaleY, cardRotation)
            startDelay = 200
            start()
        }

        val titleAlpha = ObjectAnimator.ofFloat(binding.txtTitle, "alpha", 0f, 1f).apply {
            duration = 600
        }
        val titleTransY = ObjectAnimator.ofFloat(binding.txtTitle, "translationY", 50f, 0f).apply {
            duration = 600
            interpolator = DecelerateInterpolator()
        }
        AnimatorSet().apply {
            playTogether(titleAlpha, titleTransY)
            startDelay = 800
            start()
        }

        val descAlpha = ObjectAnimator.ofFloat(binding.txtDescription, "alpha", 0f, 1f).apply {
            duration = 600
        }
        val descTransY = ObjectAnimator.ofFloat(binding.txtDescription, "translationY", 40f, 0f).apply {
            duration = 600
            interpolator = DecelerateInterpolator()
        }
        AnimatorSet().apply {
            playTogether(descAlpha, descTransY)
            startDelay = 1200
            start()
        }

        if (binding.featuresContainer.visibility == View.VISIBLE) {
            ObjectAnimator.ofFloat(binding.featuresContainer, "alpha", 0f, 1f).apply {
                duration = 400
                startDelay = 1600
                start()
            }

            animateFeatureCard(binding.feature1, 1800)
            animateFeatureCard(binding.feature2, 2000)
            animateFeatureCard(binding.feature3, 2200)
        }
    }

    private fun animateFeatureCard(card: View, delay: Long) {
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

    private fun animateDecorativeCircles(circle1: View, circle2: View) {
        val circle1TransY = ObjectAnimator.ofFloat(circle1, "translationY", 0f, 30f, 0f).apply {
            duration = 3000
            repeatCount = ObjectAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }
        val circle1TransX = ObjectAnimator.ofFloat(circle1, "translationX", 0f, -20f, 0f).apply {
            duration = 3000
            repeatCount = ObjectAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }
        AnimatorSet().apply {
            playTogether(circle1TransY, circle1TransX)
            start()
        }

        val circle2TransY = ObjectAnimator.ofFloat(circle2, "translationY", 0f, -25f, 0f).apply {
            duration = 3500
            repeatCount = ObjectAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }
        val circle2TransX = ObjectAnimator.ofFloat(circle2, "translationX", 0f, 20f, 0f).apply {
            duration = 3500
            repeatCount = ObjectAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }
        AnimatorSet().apply {
            playTogether(circle2TransY, circle2TransX)
            start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}