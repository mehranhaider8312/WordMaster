package com.codewithmehran.wordmaster.view.activities

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.codewithmehran.wordmaster.R
import com.codewithmehran.wordmaster.adapters.SwipeAbleWordAdapter
import com.codewithmehran.wordmaster.databinding.ActivityMainBinding
import com.codewithmehran.wordmaster.databinding.DialogExitConfirmationBinding
import com.codewithmehran.wordmaster.model.Word
import com.codewithmehran.wordmaster.model.WordMasterApp
import com.codewithmehran.wordmaster.view.dialogs.AddWordDialog
import com.codewithmehran.wordmaster.viewmodel.StreakViewModel
import com.codewithmehran.wordmaster.viewmodel.StreakViewModelFactory
import com.codewithmehran.wordmaster.viewmodel.WordViewModel
import com.codewithmehran.wordmaster.viewmodel.WordViewModelFactory
import com.google.android.gms.ads.AdSize
import com.yalantis.library.KolodaListener
import java.util.Locale

class MainActivity : BaseAdActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var addWordDialog: AddWordDialog
    private var exitDialog: AlertDialog? = null
    private var swipeAdapter: SwipeAbleWordAdapter? = null
    private lateinit var textToSpeech: TextToSpeech

    private val wordViewModel: WordViewModel by viewModels {
        WordViewModelFactory((application as WordMasterApp).wordRepository)
    }

    private val streakViewModel: StreakViewModel by viewModels {
        StreakViewModelFactory((application as WordMasterApp).streakRepository)
    }

    private var isInitialLoad = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initTextToSpeech()
        setupKoloda()
        setupClicks()
        setupBannerAd()
        observeViewModels()
        startStreakAnimations()

        showEmptyState()

        wordViewModel.loadWords()
        streakViewModel.loadStreak()

        onBackPressedDispatcher.addCallback(this) {
            showExitDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        wordViewModel.loadWords()
    }

    private fun initTextToSpeech() {
        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.US)
            }
        }
    }

    private fun setupKoloda() {
        swipeAdapter = SwipeAbleWordAdapter(context = this, data = emptyList())
        binding.swipeAbleCardView.adapter = swipeAdapter

        swipeAdapter?.setOnListenClickListener { wordText ->
            speakWord(wordText)
        }
        swipeAdapter?.setOnDeckEmptyListener {
            runOnUiThread {
                showEmptyState()
            }
        }

        binding.swipeAbleCardView.kolodaListener = object : KolodaListener {
            override fun onNewTopCard(position: Int) {
                Log.d("CardCountIssue", "onNewTopCard: position=$position")
                // Check if we've reached the dummy card (position 0)
                val realCount = swipeAdapter?.getRealItemCount() ?: 0
                Log.d("CardCountIssue", "Real cards remaining: $realCount")

                if (position == 0 && realCount == 0) {
                    // We're at the dummy card and no real cards left
                    showEmptyState()
                }
            }

            override fun onCardDrag(position: Int, cardView: View, progress: Float) {}

            override fun onCardSwipedLeft(position: Int) {
                Log.d("CardCountIssue", "Card swiped left at position: $position")
                // Delay to let Koloda update its internal state
                binding.root.postDelayed({
                    val realCount = swipeAdapter?.getRealItemCount() ?: 0
                    Log.d("CardCountIssue", "After swipe - Real cards remaining: $realCount")

                    // Check if this was the last real card
                    // Position parameter here is the OLD position before swipe
                    // If we had N real cards and swiped the last one (position = N),
                    // then we should have 0 real cards left
                    val wasLastCard = (swipeAdapter?.getRealItemCountBeforeSwipe(position) ?: 0) == 1

                    if (wasLastCard) {
                        Log.d("CardCountIssue", "Last card swiped, showing empty state")
                        showEmptyState()
                    }
                }, 300)
            }

            override fun onCardSwipedRight(position: Int) {
                Log.d("CardCountIssue", "Card swiped right at position: $position")
                // Same logic as left swipe
                binding.root.postDelayed({
                    val realCount = swipeAdapter?.getRealItemCount() ?: 0
                    Log.d("CardCountIssue", "After swipe - Real cards remaining: $realCount")

                    val wasLastCard = (swipeAdapter?.getRealItemCountBeforeSwipe(position) ?: 0) == 1

                    if (wasLastCard) {
                        Log.d("CardCountIssue", "Last card swiped, showing empty state")
                        showEmptyState()
                    }
                }, 300)
            }

            override fun onClickRight(position: Int) {}
            override fun onClickLeft(position: Int) {}
            override fun onCardSingleTap(position: Int) {}
            override fun onCardDoubleTap(position: Int) {}
            override fun onCardLongPress(position: Int) {}

            override fun onEmptyDeck() {
                Log.d("CardCountIssue", "Koloda onEmptyDeck called - Ignoring")
                // We'll handle empty state ourselves in onCardSwipedLeft/Right
                // Ignore Koloda's onEmptyDeck as it's unreliable with our dummy card
            }
        }
    }

    private fun handleCardSwipe(position: Int) {
        binding.root.postDelayed({
            // After swipe, check if we're at the last card
            val adapter = swipeAdapter
            if (adapter != null) {
                val remainingRealCards = adapter.getRealItemCount()
                Log.d("CardCountIssue", "After swipe - Remaining real cards: $remainingRealCards")

                // Show empty state only when NO real cards are left
                if (remainingRealCards == 0) {
                    showEmptyState()
                }
            }
        }, 300)
    }

    private fun updateDeckWithWords(words: List<Any>) {
        runOnUiThread {
            val wordList = words as List<Word>

            val hasSystemWord = wordList.isNotEmpty() && wordList[0].dateAdded.time == Long.MAX_VALUE
            val isEffectivelyEmpty = wordList.isEmpty() || (wordList.size == 1 && hasSystemWord)

            if (isEffectivelyEmpty) {
                showEmptyState()
                swipeAdapter?.updateData(emptyList())
                isInitialLoad = false
                return@runOnUiThread
            }

            val displayList = if (hasSystemWord) wordList.drop(1) else wordList
            val reversedList = displayList.reversed()

            swipeAdapter?.updateData(reversedList)

            if (displayList.isNotEmpty()) {
                showCardState()
            }

            binding.root.postDelayed({
                try {
                    binding.swipeAbleCardView.adapter = swipeAdapter
                    binding.swipeAbleCardView.reloadAdapterData()
                    isInitialLoad = false

                    Log.d("CardCountIssue", "Deck updated with ${displayList.size} cards")
                    Log.d("CardCountIssue", "Adapter count: ${swipeAdapter?.count}")
                    Log.d("CardCountIssue", "Real item count: ${swipeAdapter?.getRealItemCount()}")
                } catch (_: Exception) {}
            }, 200)
        }
    }

    private fun showEmptyState() {
        binding.emptyStateContainer.visibility = View.VISIBLE
        binding.cardContainer.visibility = View.GONE
        binding.lottieEmptyState.playAnimation()
    }

    private fun showCardState() {
        binding.emptyStateContainer.visibility = View.GONE
        binding.cardContainer.visibility = View.VISIBLE
    }

    private fun speakWord(wordText: String) {
        textToSpeech.speak(wordText, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    private fun setupClicks() {
        binding.btnMenu.setOnClickListener {
            showCustomMenuPopup()
        }
        binding.btnAddWord.setOnClickListener {
            showAddWordDialog()
        }
        binding.btnReview.setOnClickListener {
            startActivity(Intent(this, ReviewActivity::class.java))
        }
        binding.ivReloadWords.setOnClickListener {
            reloadWords()
        }
    }

    private fun reloadWords() {
        wordViewModel.words.value?.let { words ->
            val userWords = words.filter { it.dateAdded.time != Long.MAX_VALUE }

            if (userWords.isEmpty()) {
                showEmptyState()
            } else {
                swipeAdapter?.clearData()
                binding.swipeAbleCardView.reloadAdapterData()

                swipeAdapter?.updateData(userWords.reversed())
                showCardState()

                binding.root.postDelayed({
                    binding.swipeAbleCardView.reloadAdapterData()
                }, 100)
            }
        }
    }

    private fun showCustomMenuPopup() {
        val popupMenu = PopupMenu(this, binding.btnMenu)
        popupMenu.menuInflater.inflate(R.menu.main_menu, popupMenu.menu)
        popupMenu.show()
    }

    private fun showAddWordDialog() {
        addWordDialog = AddWordDialog(this) { word, meaning, synonyms, antonyms, example, source ->
            wordViewModel.addWord(word, meaning, synonyms, antonyms, example, source) {
                runOnUiThread {
                    streakViewModel.onWordAdded()
                    wordViewModel.loadWords()
                }
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
            updateDeckWithWords(words)
        }

        streakViewModel.streak.observe(this) { streak ->
            binding.txtStreak.text = getString(R.string.streak_days, streak.currentStreak)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        exitDialog?.dismiss()
        textToSpeech.stop()
        textToSpeech.shutdown()
    }
}
