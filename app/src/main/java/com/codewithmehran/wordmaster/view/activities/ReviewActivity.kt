package com.codewithmehran.wordmaster.view.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.codewithmehran.wordmaster.R
import com.codewithmehran.wordmaster.adapters.LibraryWordAdapter
import com.codewithmehran.wordmaster.databinding.ActivityReviewBinding
import com.codewithmehran.wordmaster.model.WordMasterApp
import com.codewithmehran.wordmaster.view.dialogs.EditWordDialog
import com.codewithmehran.wordmaster.viewmodel.WordViewModel
import com.codewithmehran.wordmaster.viewmodel.WordViewModelFactory
import com.codewithmehran.wordmaster.utils.MixpanelHelper

class ReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReviewBinding
    private lateinit var adapter: LibraryWordAdapter

    private val wordViewModel: WordViewModel by viewModels {
        WordViewModelFactory((application as WordMasterApp).wordRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupSearch()
        observeViewModel()

        wordViewModel.loadWords()
        
        MixpanelHelper.trackEvent("Screen View", mapOf("Screen Name" to "ReviewActivity"))
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        adapter = LibraryWordAdapter(
            onEditClick = { word ->
                showEditWordDialog(word)
            },
            onDeleteClick = { word ->
                showDeleteConfirmationDialog(word)
            }
        )

        binding.recyclerWords.layoutManager = LinearLayoutManager(this)
        binding.recyclerWords.adapter = adapter
    }

    private fun setupSearch() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                wordViewModel.filterWords(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun observeViewModel() {
        wordViewModel.filteredWords.observe(this) { words ->
            val processedWords = if (words.isNotEmpty()) {
                if (words[0].dateAdded.time == Long.MAX_VALUE) {
                    words.drop(1)
                } else {
                    words
                }
            } else {
                emptyList()
            }

            adapter.submitList(processedWords)

            binding.emptyStateContainer.visibility =
                if (processedWords.isEmpty()) View.VISIBLE
                else View.GONE
            if (processedWords.isEmpty())
                binding.lottieEmptyState.playAnimation()
        }
    }

    private fun showEditWordDialog(word: com.codewithmehran.wordmaster.model.Word) {
        val editDialog = EditWordDialog(
            context = this,
            originalWord = word,
            onWordUpdated = { updatedWord ->
                wordViewModel.updateWord(updatedWord)
            }
        )
        editDialog.show()
    }

    private fun showDeleteConfirmationDialog(word: com.codewithmehran.wordmaster.model.Word) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_delete_confirmation, null)
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val tvMessage = dialogView.findViewById<android.widget.TextView>(R.id.tvMessage)
        val btnDelete = dialogView.findViewById<View>(R.id.btnDelete)
        val btnCancel = dialogView.findViewById<View>(R.id.btnCancel)
        val adContainer = dialogView.findViewById<android.widget.FrameLayout>(R.id.adContainer)

        tvMessage.text = getString(R.string.delete_confirmation_message, word.word)

        val app = application as WordMasterApp
        val adView = app.createBannerAdView(this, com.google.android.gms.ads.AdSize.BANNER)
        adContainer.removeAllViews()
        adContainer.addView(adView)

        btnDelete.setOnClickListener {
            wordViewModel.deleteWord(word)
            MixpanelHelper.trackWordDeleted(word.word)
            dialog.dismiss()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}