package com.codewithmehran.wordmaster.view.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.codewithmehran.wordmaster.R
import com.codewithmehran.wordmaster.model.WordMasterApp
import com.codewithmehran.wordmaster.databinding.ActivityAddWordBinding
import com.codewithmehran.wordmaster.viewmodel.StreakViewModel
import com.codewithmehran.wordmaster.viewmodel.StreakViewModelFactory
import com.codewithmehran.wordmaster.viewmodel.WordViewModel
import com.codewithmehran.wordmaster.viewmodel.WordViewModelFactory

class AddWordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddWordBinding

    private val wordViewModel: WordViewModel by viewModels {
        WordViewModelFactory((application as WordMasterApp).wordRepository)
    }

    private val streakViewModel: StreakViewModel by viewModels {
        StreakViewModelFactory((application as WordMasterApp).streakRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddWordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUi()
    }

    private fun setupUi() {
        binding.toolbar.title = getString(R.string.add_new_word)
        binding.toolbar.setNavigationIcon(R.drawable.arrow_back)
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.btnSave.setOnClickListener {
            val word = binding.inputWord.text.toString()
            val meaning = binding.inputMeaning.text.toString()
            val synonyms = binding.inputSynonyms.text.toString()
            val antonyms = binding.inputAntonyms.text.toString()
            val example = binding.inputExample.text.toString()

            if (word.isBlank() || meaning.isBlank() || synonyms.isBlank() || antonyms.isBlank()) {
                return@setOnClickListener
            }

            wordViewModel.addWord(
                wordText = word,
                meaning = meaning,
                synonyms = synonyms,
                antonyms = antonyms,
                exampleSentence = example
            ) {
                streakViewModel.onWordAdded()
                finish()
            }
        }

        binding.btnCancel.setOnClickListener { finish() }
    }
}


