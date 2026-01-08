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
    private var currentWordCount = 0 // Track actual word count
    private var isResuming = false // Track if we're resuming
    private var shouldForceReset = false // Flag to force deck reset

    private val wordViewModel: WordViewModel by viewModels {
        WordViewModelFactory((application as WordMasterApp).wordRepository)
    }

    private val streakViewModel: StreakViewModel by viewModels {
        StreakViewModelFactory((application as WordMasterApp).streakRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("CardCountIssue", "MainActivity onCreate started")

        initTextToSpeech()

        setupKoloda()
        setupClicks()
        setupBannerAd()
        observeViewModels()
        startStreakAnimations()

        wordViewModel.loadWords()
        streakViewModel.loadStreak()

        onBackPressedDispatcher.addCallback(this) {
            showExitDialog()
        }

        Log.d("CardCountIssue", "MainActivity onCreate completed")
    }

    override fun onResume() {
        super.onResume()
        isResuming = true
        Log.d("CardCountIssue", "MainActivity onResume - reloading words")
        wordViewModel.loadWords()

        // Clear the isResuming flag after a delay
        binding.root.postDelayed({
            isResuming = false
        }, 1000) // 1 second delay should be enough
    }

    override fun onPause() {
        super.onPause()
        isResuming = false
    }

    private fun initTextToSpeech() {
        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "TTS language not supported", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "TTS initialization failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupKoloda() {
        Log.d("CardCountIssue", "setupKoloda called - initializing adapter with empty list")

        // Initialize adapter with empty list first
        swipeAdapter = SwipeAbleWordAdapter(context = this, data = emptyList())
        binding.swipeAbleCardView.adapter = swipeAdapter

        swipeAdapter?.setOnListenClickListener { wordText ->
            speakWord(wordText)
        }

        swipeAdapter?.setOnCardAnimationListener { view, onAnimationComplete ->
            flipAndBounceAnimation(view, onAnimationComplete)
        }

        binding.swipeAbleCardView.kolodaListener = object : KolodaListener {
            override fun onNewTopCard(position: Int) {
                Log.d("CardCountIssue", "Koloda onNewTopCard: position=$position, currentWordCount=$currentWordCount")
            }

            override fun onCardDrag(position: Int, cardView: View, progress: Float) {
            }

            override fun onCardSwipedLeft(position: Int) {
                Log.d("CardCountIssue", "Koloda onCardSwipedLeft: position=$position")
                handleCardSwipe()
            }

            override fun onCardSwipedRight(position: Int) {
                Log.d("CardCountIssue", "Koloda onCardSwipedRight: position=$position")
                handleCardSwipe()
            }

            override fun onClickRight(position: Int) {
            }

            override fun onClickLeft(position: Int) {
            }

            override fun onCardSingleTap(position: Int) {
            }

            override fun onCardDoubleTap(position: Int) {
            }

            override fun onCardLongPress(position: Int) {
            }

            override fun onEmptyDeck() {
                Log.d("CardCountIssue", "Koloda onEmptyDeck called - currentWordCount=$currentWordCount, isResuming=$isResuming")

                // Only show empty state if we're not resuming and there are actually no words
                if (!isResuming && currentWordCount == 0) {
                    showEmptyState()
                } else if (isResuming) {
                    // We're resuming, ignore the onEmptyDeck call and check manually
                    checkIfDeckEmpty()
                }
            }
        }

        val fadeInAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        binding.swipeAbleCardView.startAnimation(fadeInAnimation)

        Log.d("CardCountIssue", "setupKoloda completed")
    }

    private fun handleCardSwipe() {
        // Use postDelayed to check after swipe animation completes
        binding.root.postDelayed({
            val adapterCount = swipeAdapter?.count ?: 0
            Log.d("CardCountIssue", "handleCardSwipe - adapter count: $adapterCount, currentWordCount: $currentWordCount")

            if (adapterCount == 0) {
                if (currentWordCount == 0) {
                    showEmptyState()
                } else {
                    // We have words but adapter is empty - refresh the deck
                    refreshDeck()
                }
            }
        }, 300) // Small delay to let Koloda update
    }

    private fun checkIfDeckEmpty() {
        // Use postDelayed to check after swipe animation completes
        binding.root.postDelayed({
            val adapterCount = swipeAdapter?.count ?: 0
            Log.d("CardCountIssue", "checkIfDeckEmpty - adapter count: $adapterCount, currentWordCount: $currentWordCount")

            if (adapterCount == 0 && currentWordCount == 0) {
                showEmptyState()
            } else if (adapterCount == 0 && currentWordCount > 0) {
                // Adapter says empty but we have words - reset the deck
                resetDeck()
            }
        }, 300) // Small delay to let Koloda update
    }

    private fun refreshDeck() {
        Log.d("CardCountIssue", "refreshDeck called - refreshing with latest data")

        wordViewModel.words.value?.let { words ->
            if (words.isNotEmpty()) {
                Log.d("CardCountIssue", "Refreshing deck with ${words.size} words")

                // Create a new adapter instance to ensure clean state
                swipeAdapter = SwipeAbleWordAdapter(context = this, data = words)

                // Re-attach listeners
                swipeAdapter?.setOnListenClickListener { wordText ->
                    speakWord(wordText)
                }

                swipeAdapter?.setOnCardAnimationListener { view, onAnimationComplete ->
                    flipAndBounceAnimation(view, onAnimationComplete)
                }

                // Set the new adapter
                binding.swipeAbleCardView.adapter = swipeAdapter

                Log.d("CardCountIssue", "New adapter set with ${words.size} words")
            }
        }
    }

    private fun resetDeck() {
        Log.d("CardCountIssue", "resetDeck called - currentWordCount=$currentWordCount")

        wordViewModel.words.value?.let { words ->
            if (words.isNotEmpty()) {
                showCardState()

                // Recreate adapter
                swipeAdapter = SwipeAbleWordAdapter(context = this, data = words)

                swipeAdapter?.setOnListenClickListener { wordText ->
                    speakWord(wordText)
                }

                swipeAdapter?.setOnCardAnimationListener { view, onAnimationComplete ->
                    flipAndBounceAnimation(view, onAnimationComplete)
                }

                binding.swipeAbleCardView.adapter = swipeAdapter

                Log.d("CardCountIssue", "Deck reset with ${words.size} words")
            }
        }
    }

    private fun showEmptyState() {
        Log.d("CardCountIssue", "showEmptyState called")
        binding.emptyStateContainer.visibility = View.VISIBLE
        binding.cardContainer.visibility = View.GONE
        binding.lottieEmptyState.playAnimation()
    }

    private fun showCardState() {
        Log.d("CardCountIssue", "showCardState called")
        binding.emptyStateContainer.visibility = View.GONE
        binding.cardContainer.visibility = View.VISIBLE
    }

    private fun flipAndBounceAnimation(view: View, onAnimationComplete: () -> Unit) {
        val flipAnimation = AnimationUtils.loadAnimation(this, R.anim.flip_360)
        val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce)

        view.startAnimation(flipAnimation)

        flipAnimation.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
            override fun onAnimationStart(animation: android.view.animation.Animation?) {}

            override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                view.startAnimation(bounceAnimation)
                bounceAnimation.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
                    override fun onAnimationStart(animation: android.view.animation.Animation?) {}

                    override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                        onAnimationComplete()
                    }

                    override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
                })
            }

            override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
        })
    }

    private fun speakWord(wordText: String) {
        textToSpeech.speak(wordText, TextToSpeech.QUEUE_FLUSH, null, null)
        Toast.makeText(this, "Speaking: $wordText", Toast.LENGTH_SHORT).show()
    }

    private fun setupClicks() {
        binding.btnMenu.setOnClickListener {
            trackClick()
            showCustomMenuPopup()
        }

        binding.btnAddWord.setOnClickListener {
            trackClick()
            showAddWordDialog()
        }

        binding.btnReview.setOnClickListener {
            trackClick()
            startActivity(Intent(this, ReviewActivity::class.java))
        }

        binding.ivReloadWords.setOnClickListener {
            trackClick()
            reloadWords()
        }
    }

    private fun reloadWords() {
        Log.d("CardCountIssue", "reloadWords button clicked")

        wordViewModel.words.value?.let { words ->
            Log.d("CardCountIssue", "Current words in ViewModel: ${words.size}")

            if (words.isEmpty()) {
                Toast.makeText(this, "Add words first before reloading", Toast.LENGTH_SHORT).show()
                showEmptyState()
            } else {
                Toast.makeText(this, "Words reloaded!", Toast.LENGTH_SHORT).show()
                resetDeck() // Use resetDeck instead of custom logic
            }
        } ?: run {
            Log.d("CardCountIssue", "ViewModel words is null")
            Toast.makeText(this, "Add words first before reloading", Toast.LENGTH_SHORT).show()
            showEmptyState()
        }
    }

    private fun showCustomMenuPopup() {
        val popupMenu = PopupMenu(this, binding.btnMenu)
        popupMenu.menuInflater.inflate(R.menu.main_menu, popupMenu.menu)

        try {
            val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
            fieldMPopup.isAccessible = true
            val mPopup = fieldMPopup.get(popupMenu)
            mPopup.javaClass
                .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                .invoke(mPopup, true)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
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
        popupMenu.show()
    }

    private fun showAddWordDialog() {
        Log.d("CardCountIssue", "showAddWordDialog called")

        addWordDialog = AddWordDialog(this) { word, meaning, synonyms, antonyms, example, source ->
            Log.d("CardCountIssue", "AddWordDialog save callback - Adding word: $word")

            wordViewModel.addWord(
                wordText = word,
                meaning = meaning,
                synonyms = synonyms,
                antonyms = antonyms,
                exampleSentence = example,
                source = source
            ) {
                Log.d("CardCountIssue", "Word added to database - completion callback")
                runOnUiThread {
                    streakViewModel.onWordAdded()
                    // Load words will trigger the observer
                    wordViewModel.loadWords()

                    // After adding a word, make sure cards are visible
                    if (binding.cardContainer.visibility != View.VISIBLE) {
                        showCardState()
                    }

                    // Set flag to force reset on next update
                    shouldForceReset = true
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
        Log.d("CardCountIssue", "Setting up ViewModel observers")

        wordViewModel.words.observe(this) { words ->
            Log.d("CardCountIssue", "====== ViewModel words changed ======")
            Log.d("CardCountIssue", "Received ${words.size} words from ViewModel")

            currentWordCount = words.size

            words.forEachIndexed { index, word ->
                Log.d("CardCountIssue", "Word $index: ${word.word}")
            }

            if (words.isEmpty()) {
                Log.d("CardCountIssue", "Words list is empty - showing empty state")
                showEmptyState()
            } else {
                Log.d("CardCountIssue", "Words list has ${words.size} items - checking UI state")

                // Always show card state when we have words
                showCardState()

                // Check if we need to force reset (e.g., after adding a word)
                if (shouldForceReset || swipeAdapter == null) {
                    Log.d("CardCountIssue", "Forcing deck reset")

                    // Create new adapter
                    swipeAdapter = SwipeAbleWordAdapter(context = this, data = words)

                    // Re-attach listeners
                    swipeAdapter?.setOnListenClickListener { wordText ->
                        speakWord(wordText)
                    }

                    swipeAdapter?.setOnCardAnimationListener { view, onAnimationComplete ->
                        flipAndBounceAnimation(view, onAnimationComplete)
                    }

                    // Set the new adapter
                    binding.swipeAbleCardView.adapter = swipeAdapter

                    // Reset the flag
                    shouldForceReset = false

                    Log.d("CardCountIssue", "New adapter created with ${words.size} words")
                } else {
                    // Just update existing adapter data
                    swipeAdapter?.updateData(words)
                    Log.d("CardCountIssue", "Existing adapter updated with ${words.size} words")
                }
            }

            Log.d("CardCountIssue", "====== Observer update complete ======")
        }

        streakViewModel.streak.observe(this) { streak ->
            binding.txtStreak.text = getString(R.string.streak_days, streak.currentStreak)
            val pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse)
            binding.txtStreak.startAnimation(pulseAnimation)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        exitDialog?.dismiss()
        textToSpeech.stop()
        textToSpeech.shutdown()
    }
}