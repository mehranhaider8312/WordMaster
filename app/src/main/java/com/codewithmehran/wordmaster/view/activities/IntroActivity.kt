package com.codewithmehran.wordmaster.view.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.codewithmehran.wordmaster.R

class IntroActivity : AppCompatActivity() {

    private lateinit var binding: com.codewithmehran.wordmaster.databinding.ActivityIntroBinding
    private val sharedPreferences by lazy { getSharedPreferences("settings", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        if (!sharedPreferences.getBoolean("is_first_launch", true)) {
            startActivity(android.content.Intent(this, MainActivity::class.java))
            finish()
            return
        }

        enableEdgeToEdge()
        binding = com.codewithmehran.wordmaster.databinding.ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupViewPager()
        setupListeners()
    }

    private fun setupViewPager() {
        val fragments = listOf(
            com.codewithmehran.wordmaster.view.fragments.IntroFragment.newInstance(
                "Build Vocabulary",
                "Learn new words daily and improve your language skills effectively.",
                R.drawable.logo_app
            ),
            com.codewithmehran.wordmaster.view.fragments.IntroFragment.newInstance(
                "Take Quizzes",
                "Test your knowledge with interactive quizzes and track your progress.",
                R.drawable.logo_app
            ),
            com.codewithmehran.wordmaster.view.fragments.IntroFragment.newInstance(
                "Multi-Language",
                "Support for multiple languages including Urdu, Hindi, and Arabic.",
                R.drawable.logo_app
            )
        )

        val adapter = com.codewithmehran.wordmaster.view.adapters.IntroAdapter(this, fragments)
        binding.introViewPager.adapter = adapter
        
        binding.introViewPager.registerOnPageChangeCallback(object : androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == fragments.size - 1) {
                    binding.btnNext.text = "START"
                    binding.btnBack.visibility = android.view.View.VISIBLE
                    binding.tvSkip.visibility = android.view.View.INVISIBLE
                } else {
                    binding.btnNext.text = "NEXT"
                    binding.btnBack.visibility = if (position > 0) android.view.View.VISIBLE else android.view.View.GONE
                    binding.tvSkip.visibility = android.view.View.VISIBLE
                }
            }
        })
    }

    private fun setupListeners() {
        binding.btnNext.setOnClickListener {
            val currentItem = binding.introViewPager.currentItem
            if (currentItem < (binding.introViewPager.adapter?.itemCount ?: 0) - 1) {
                binding.introViewPager.currentItem = currentItem + 1
            } else {
                completeIntro()
            }
        }

        binding.btnBack.setOnClickListener {
            val currentItem = binding.introViewPager.currentItem
            if (currentItem > 0) {
                binding.introViewPager.currentItem = currentItem - 1
            }
        }

        binding.tvSkip.setOnClickListener {
            completeIntro()
        }
    }

    private fun completeIntro() {
        sharedPreferences.edit().putBoolean("is_first_launch", false).apply()
        startActivity(android.content.Intent(this, MainActivity::class.java))
        finish()
    }
}