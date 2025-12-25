package com.codewithmehran.wordmaster.view.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codewithmehran.wordmaster.R
import com.codewithmehran.wordmaster.model.WordMasterApp
import com.codewithmehran.wordmaster.adapters.WordCardAdapter
import com.codewithmehran.wordmaster.databinding.ActivityMainBinding
import com.codewithmehran.wordmaster.viewmodel.StreakViewModel
import com.codewithmehran.wordmaster.viewmodel.StreakViewModelFactory
import com.codewithmehran.wordmaster.viewmodel.WordViewModel
import com.codewithmehran.wordmaster.viewmodel.WordViewModelFactory
import com.google.android.gms.ads.AdSize

class MainActivity : BaseAdActivity() {

    private lateinit var binding: ActivityMainBinding

    private val wordViewModel: WordViewModel by viewModels {
        WordViewModelFactory((application as WordMasterApp).wordRepository)
    }

    private val streakViewModel: StreakViewModel by viewModels {
        StreakViewModelFactory((application as WordMasterApp).streakRepository)
    }

    private lateinit var adapter: WordCardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecycler()
        setupClicks()
        setupBannerAd()
        observeViewModels()

        wordViewModel.loadWords()
        streakViewModel.loadStreak()
    }

    private fun setupRecycler() {
        adapter = WordCardAdapter()
        binding.recyclerWords.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerWords.adapter = adapter

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // simple swipe-to-next behavior; data order remains same
                adapter.notifyItemChanged(viewHolder.bindingAdapterPosition)
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.recyclerWords)
    }

    private fun setupClicks() {
        binding.btnAddWord.setOnClickListener {
            trackClick() // Track click for interstitial ad
            startActivity(Intent(this, AddWordActivity::class.java))
        }
        binding.btnReview.setOnClickListener {
            trackClick() // Track click for interstitial ad
            startActivity(Intent(this, QuizActivity::class.java))
        }
        binding.btnSettings.setOnClickListener {
            trackClick() // Track click for interstitial ad
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun setupBannerAd() {
        val app = application as WordMasterApp
        val bannerAd = app.createBannerAdView(this, AdSize.BANNER)
        binding.bannerAdContainer.removeAllViews()
        binding.bannerAdContainer.addView(bannerAd)
    }

    private fun observeViewModels() {
        wordViewModel.words.observe(this) { words ->
            adapter.submitList(words)
            binding.txtEmptyState.visibility =
                if (words.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
        }

        streakViewModel.streak.observe(this) { streak ->
            binding.txtStreak.text =
                getString(R.string.streak_days, streak.currentStreak)
        }
    }
}
