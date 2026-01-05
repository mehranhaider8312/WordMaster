package com.codewithmehran.wordmaster.view.activities

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.PopupMenu
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codewithmehran.wordmaster.R
import com.codewithmehran.wordmaster.model.WordMasterApp
import com.codewithmehran.wordmaster.adapters.WordCardAdapter
import com.codewithmehran.wordmaster.databinding.ActivityMainBinding
import com.codewithmehran.wordmaster.databinding.DialogExitConfirmationBinding
import com.codewithmehran.wordmaster.view.dialogs.AddWordDialog
import com.codewithmehran.wordmaster.viewmodel.StreakViewModel
import com.codewithmehran.wordmaster.viewmodel.StreakViewModelFactory
import com.codewithmehran.wordmaster.viewmodel.WordViewModel
import com.codewithmehran.wordmaster.viewmodel.WordViewModelFactory
import com.google.android.gms.ads.AdSize

class MainActivity : BaseAdActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var addWordDialog: AddWordDialog
    private var exitDialog: AlertDialog? = null

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
        startStreakAnimations()

        wordViewModel.loadWords()
        streakViewModel.loadStreak()
        onBackPressedDispatcher.addCallback(this) {
            showExitDialog()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        adapter.onDestroy()
        exitDialog?.dismiss()
    }

    private fun setupRecycler() {
        adapter = WordCardAdapter()
        binding.recyclerWords.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerWords.adapter = adapter

        val fadeInAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        binding.recyclerWords.startAnimation(fadeInAnimation)

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
                adapter.notifyItemChanged(viewHolder.bindingAdapterPosition)
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.recyclerWords)
    }

    private fun setupClicks() {
        binding.btnMenu.setOnClickListener {
            trackClick()
            showMenuPopup()
        }

        binding.btnAddWord.setOnClickListener {
            trackClick()
            showAddWordDialog()
        }

        binding.btnReview.setOnClickListener {
            trackClick()
            startActivity(Intent(this, ReviewActivity::class.java))
        }
    }

    private fun showMenuPopup() {
        val popup = PopupMenu(this, binding.btnMenu)
        popup.menuInflater.inflate(R.menu.main_menu, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_about -> {
                    startActivity(Intent(this, AboutActivity::class.java))
                    true
                }
                R.id.menu_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun showAddWordDialog() {
        addWordDialog = AddWordDialog(this) { word, meaning, synonyms, antonyms, example ->
            wordViewModel.addWord(
                wordText = word,
                meaning = meaning,
                synonyms = synonyms,
                antonyms = antonyms,
                exampleSentence = example
            ) {
                streakViewModel.onWordAdded()

                val slideInAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_right)
                binding.recyclerWords.startAnimation(slideInAnimation)
            }
        }
        addWordDialog.show()
    }

    private fun showExitDialog() {
        val dialogBinding = DialogExitConfirmationBinding.inflate(layoutInflater)

        val app = application as WordMasterApp
        val mediumRectangleAd = app.createBannerAdView(this, AdSize.MEDIUM_RECTANGLE)
        dialogBinding.adContainer.removeAllViews()
        dialogBinding.adContainer.addView(mediumRectangleAd)
        dialogBinding.tvAdPlaceholder.visibility = android.view.View.GONE

        exitDialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setCancelable(true)
            .create()

        dialogBinding.btnCancel.setOnClickListener {
            exitDialog?.dismiss()
        }

        dialogBinding.btnExit.setOnClickListener {
            exitDialog?.dismiss()
            finishAffinity()
        }

        exitDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        exitDialog?.show()
    }

    private fun setupBannerAd() {
        val app = application as WordMasterApp
        val bannerAd = app.createBannerAdView(this, AdSize.BANNER)
        binding.bannerAdContainer.removeAllViews()
        binding.bannerAdContainer.addView(bannerAd)
    }

    private fun startStreakAnimations() {
        val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce)
        binding.txtStreak.startAnimation(bounceAnimation)
    }

    private fun observeViewModels() {
        wordViewModel.words.observe(this) { words ->
            adapter.submitList(words)
//            binding.txtEmptyState.visibility =
//                if (words.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE

            if (words.isNotEmpty()) {
                val scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_in)
                binding.recyclerWords.startAnimation(scaleAnimation)
            }
        }

        streakViewModel.streak.observe(this) { streak ->
            binding.txtStreak.text =
                getString(R.string.streak_days, streak.currentStreak)

            val pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse)
            binding.txtStreak.startAnimation(pulseAnimation)
        }
    }
}