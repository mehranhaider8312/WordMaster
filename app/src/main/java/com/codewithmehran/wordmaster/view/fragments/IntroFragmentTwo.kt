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

class IntroFragmentTwo : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_intro_two, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startAnimations(view)
    }

    private fun startAnimations(view: View) {
        val txtTitle = view.findViewById<TextView>(R.id.txtTitle)
        val txtDescription = view.findViewById<TextView>(R.id.txtDescription)
        val cardStreak = view.findViewById<CardView>(R.id.cardStreak)
        val txtFireEmoji = view.findViewById<TextView>(R.id.txtFireEmoji)
        val txtStreakCount = view.findViewById<TextView>(R.id.txtStreakCount)
        val txtStreakLabel = view.findViewById<TextView>(R.id.txtStreakLabel)
        val weeklyCalendar = view.findViewById<LinearLayout>(R.id.weeklyCalendar)
        val featuresContainer = view.findViewById<LinearLayout>(R.id.featuresContainer)
        val feature1 = view.findViewById<LinearLayout>(R.id.feature1)
        val feature2 = view.findViewById<LinearLayout>(R.id.feature2)
        val feature3 = view.findViewById<LinearLayout>(R.id.feature3)
        val decorStar1 = view.findViewById<View>(R.id.decorStar1)
        val decorStar2 = view.findViewById<View>(R.id.decorStar2)

        animateStars(decorStar1, decorStar2)

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

        val cardAlpha = ObjectAnimator.ofFloat(cardStreak, "alpha", 0f, 1f).apply {
            duration = 600
        }
        val cardScaleX = ObjectAnimator.ofFloat(cardStreak, "scaleX", 0.8f, 1.05f, 1f).apply {
            duration = 800
            interpolator = BounceInterpolator()
        }
        val cardScaleY = ObjectAnimator.ofFloat(cardStreak, "scaleY", 0.8f, 1.05f, 1f).apply {
            duration = 800
            interpolator = BounceInterpolator()
        }
        AnimatorSet().apply {
            playTogether(cardAlpha, cardScaleX, cardScaleY)
            startDelay = 1000
            start()
        }

        ObjectAnimator.ofFloat(txtFireEmoji, "scaleX", 0f, 1.3f, 1f).apply {
            duration = 700
            startDelay = 1400
            interpolator = OvershootInterpolator(2f)
            start()
        }
        ObjectAnimator.ofFloat(txtFireEmoji, "scaleY", 0f, 1.3f, 1f).apply {
            duration = 700
            startDelay = 1400
            interpolator = OvershootInterpolator(2f)
            start()
        }
        ObjectAnimator.ofFloat(txtFireEmoji, "rotation", 0f, 360f).apply {
            duration = 700
            startDelay = 1400
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }

        view.postDelayed({
            val pulsateX = ObjectAnimator.ofFloat(txtFireEmoji, "scaleX", 1f, 1.15f, 1f).apply {
                duration = 1000
                repeatCount = ObjectAnimator.INFINITE
                interpolator = AccelerateDecelerateInterpolator()
            }
            val pulsateY = ObjectAnimator.ofFloat(txtFireEmoji, "scaleY", 1f, 1.15f, 1f).apply {
                duration = 1000
                repeatCount = ObjectAnimator.INFINITE
                interpolator = AccelerateDecelerateInterpolator()
            }
            AnimatorSet().apply {
                playTogether(pulsateX, pulsateY)
                start()
            }
        }, 2100)

        ObjectAnimator.ofFloat(txtStreakCount, "alpha", 0f, 1f).apply {
            duration = 500
            startDelay = 1800
            start()
        }

        ObjectAnimator.ofFloat(txtStreakLabel, "alpha", 0f, 1f).apply {
            duration = 500
            startDelay = 2000
            start()
        }

        ObjectAnimator.ofFloat(weeklyCalendar, "alpha", 0f, 1f).apply {
            duration = 600
            startDelay = 2200
            start()
        }

        val days = listOf(
            view.findViewById<LinearLayout>(R.id.day1),
            view.findViewById<LinearLayout>(R.id.day2),
            view.findViewById<LinearLayout>(R.id.day3),
            view.findViewById<LinearLayout>(R.id.day4),
            view.findViewById<LinearLayout>(R.id.day5),
            view.findViewById<LinearLayout>(R.id.day6),
            view.findViewById<LinearLayout>(R.id.day7)
        )

        days.forEachIndexed { index, day ->
            animateDay(day, 2400L + (index * 100L))
        }

        ObjectAnimator.ofFloat(featuresContainer, "alpha", 0f, 1f).apply {
            duration = 400
            startDelay = 3200
            start()
        }

        animateFeatureCard(feature1, 3400)
        animateFeatureCard(feature2, 3600)
        animateFeatureCard(feature3, 3800)
    }

    private fun animateDay(day: LinearLayout, delay: Long) {
        val scaleX = ObjectAnimator.ofFloat(day, "scaleX", 0f, 1.2f, 1f).apply {
            duration = 400
            interpolator = OvershootInterpolator()
        }
        val scaleY = ObjectAnimator.ofFloat(day, "scaleY", 0f, 1.2f, 1f).apply {
            duration = 400
            interpolator = OvershootInterpolator()
        }

        day.scaleX = 0f
        day.scaleY = 0f

        AnimatorSet().apply {
            playTogether(scaleX, scaleY)
            startDelay = delay
            start()
        }
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

    private fun animateStars(star1: View, star2: View) {
        val star1Rotation = ObjectAnimator.ofFloat(star1, "rotation", 0f, 360f).apply {
            duration = 4000
            repeatCount = ObjectAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }
        val star1Scale = ObjectAnimator.ofFloat(star1, "scaleX", 1f, 1.2f, 1f).apply {
            duration = 2000
            repeatCount = ObjectAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }
        val star1ScaleY = ObjectAnimator.ofFloat(star1, "scaleY", 1f, 1.2f, 1f).apply {
            duration = 2000
            repeatCount = ObjectAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }
        AnimatorSet().apply {
            playTogether(star1Rotation, star1Scale, star1ScaleY)
            start()
        }

        val star2Rotation = ObjectAnimator.ofFloat(star2, "rotation", 0f, -360f).apply {
            duration = 3500
            repeatCount = ObjectAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }
        val star2Scale = ObjectAnimator.ofFloat(star2, "scaleX", 1f, 1.3f, 1f).apply {
            duration = 2500
            repeatCount = ObjectAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }
        val star2ScaleY = ObjectAnimator.ofFloat(star2, "scaleY", 1f, 1.3f, 1f).apply {
            duration = 2500
            repeatCount = ObjectAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }
        AnimatorSet().apply {
            playTogether(star2Rotation, star2Scale, star2ScaleY)
            start()
        }
    }
}