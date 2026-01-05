package com.codewithmehran.wordmaster.view.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.codewithmehran.wordmaster.adapters.LibraryWordAdapter
import com.codewithmehran.wordmaster.databinding.ActivityReviewBinding
import com.codewithmehran.wordmaster.model.WordMasterApp
import com.codewithmehran.wordmaster.view.dialogs.EditWordDialog
import com.codewithmehran.wordmaster.viewmodel.WordViewModel
import com.codewithmehran.wordmaster.viewmodel.WordViewModelFactory

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
            adapter.submitList(words)

            binding.txtEmptyState.visibility =
                if (words.isEmpty()) android.view.View.VISIBLE
                else android.view.View.GONE
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
        AlertDialog.Builder(this)
            .setTitle("Delete Word")
            .setMessage("Are you sure you want to delete '${word.word}'?")
            .setPositiveButton("Delete") { _, _ ->
                wordViewModel.deleteWord(word)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

}