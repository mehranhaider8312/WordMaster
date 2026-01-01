package com.codewithmehran.wordmaster.view.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.codewithmehran.wordmaster.databinding.ActivityIntroBinding
import com.codewithmehran.wordmaster.view.adapters.IntroAdapter
import com.codewithmehran.wordmaster.view.fragments.IntroFragmentOne
import com.codewithmehran.wordmaster.view.fragments.IntroFragmentTwo
import com.codewithmehran.wordmaster.view.fragments.IntroFragmentThree

class IntroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIntroBinding
    private val sharedPreferences by lazy { getSharedPreferences("settings", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!sharedPreferences.getBoolean("is_first_launch", true)) {
            startActivity(android.content.Intent(this, MainActivity::class.java))
            finish()
            return
        }

        enableEdgeToEdge()
        binding = ActivityIntroBinding.inflate(layoutInflater)
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
            IntroFragmentOne(),
            IntroFragmentTwo(),
            IntroFragmentThree()
        )

        val adapter = IntroAdapter(this, fragments)
        binding.introViewPager.adapter = adapter

        binding.introViewPager.registerOnPageChangeCallback(object : androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == fragments.size - 1) {
                    binding.btnNext.text = "GET STARTED"
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