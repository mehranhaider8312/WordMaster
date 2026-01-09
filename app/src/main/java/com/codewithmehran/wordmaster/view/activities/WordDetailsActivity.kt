package com.codewithmehran.wordmaster.view.activities

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.codewithmehran.wordmaster.R
import com.codewithmehran.wordmaster.databinding.ActivityWordDetailsBinding
import com.codewithmehran.wordmaster.model.WordMasterApp
import com.codewithmehran.wordmaster.view.dialogs.EditWordDialog
import com.codewithmehran.wordmaster.viewmodel.WordViewModel
import com.codewithmehran.wordmaster.viewmodel.WordViewModelFactory
import java.util.Date
import java.util.Locale

class WordDetailsActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: ActivityWordDetailsBinding
    private lateinit var textToSpeech: TextToSpeech
    private var currentWordId: Long = -1
    private var isTtsReady = false

    private val wordViewModel: WordViewModel by viewModels {
        WordViewModelFactory((application as WordMasterApp).wordRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityWordDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentWordId = intent.getLongExtra("WORD_ID", -1L)

        if (currentWordId == -1L) {
            finish()
            return
        }

        setupToolbar()
        setupTextToSpeech()
        setupClickListeners()
        observeViewModel()

        wordViewModel.loadWord(currentWordId)
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech.shutdown()
    }

    private fun setupToolbar() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupTextToSpeech() {
        textToSpeech = TextToSpeech(this, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech.setLanguage(Locale.US)
            isTtsReady = if (result != TextToSpeech.LANG_MISSING_DATA &&
                result != TextToSpeech.LANG_NOT_SUPPORTED) {
                true
            } else {
                Toast.makeText(this, getString(R.string.tts_language_not_supported), Toast.LENGTH_SHORT).show()
                false
            }
        } else {
            Toast.makeText(this, getString(R.string.tts_initialization_failed), Toast.LENGTH_SHORT).show()
            isTtsReady = false
        }
    }

    private fun setupClickListeners() {
        binding.btnListen.setOnClickListener {
            val word = binding.tvWord.text.toString()
            if (word.isNotEmpty() && isTtsReady) {
                textToSpeech.speak(word, TextToSpeech.QUEUE_FLUSH, null, null)
            } else if (!isTtsReady) {
                Toast.makeText(this, getString(R.string.tts_not_ready), Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnEditWord.setOnClickListener {
            wordViewModel.selectedWord.value?.let { word ->
                showEditWordDialog(word)
            }
        }

        binding.btnDeleteWord.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    private fun observeViewModel() {
        wordViewModel.selectedWord.observe(this) { word ->
            if (word != null) {
                updateUI(word)
            }
        }
    }

    private fun updateUI(word: com.codewithmehran.wordmaster.model.Word) {
        binding.tvHeaderTitle.text = word.word
        binding.tvWord.text = word.word

        val daysAgo = calculateDaysAgo(word.dateAdded)
        binding.tvAddedDate.text = "Added ${daysAgo}d ago"

        binding.tvMeaning.text = word.meaning

        val synonymsList = word.synonyms.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        if (synonymsList.isNotEmpty()) {
            binding.chipSynonym1.text = synonymsList.first()
            binding.chipSynonym1.visibility = android.view.View.VISIBLE
        } else {
            binding.chipSynonym1.visibility = android.view.View.GONE
        }

        val antonymsList = word.antonyms.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        if (antonymsList.isNotEmpty()) {
            binding.chipAntonym1.text = antonymsList.first()
            binding.chipAntonym1.visibility = android.view.View.VISIBLE
        } else {
            binding.chipAntonym1.visibility = android.view.View.GONE
        }

        if (word.exampleSentence.isNotEmpty()) {
            binding.tvExample.text = word.exampleSentence
        } else {
            binding.tvExample.text = "No example sentence added"
        }
    }

    private fun calculateDaysAgo(dateAdded: Date): Int {
        val currentTime = System.currentTimeMillis()
        val addedTime = dateAdded.time
        val difference = currentTime - addedTime
        return (difference / (1000 * 60 * 60 * 24)).toInt()
    }

    private fun showDeleteConfirmationDialog() {
        val word = wordViewModel.selectedWord.value ?: return

        AlertDialog.Builder(this)
            .setTitle("Delete Word")
            .setMessage("Are you sure you want to delete '${word.word}'? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                wordViewModel.deleteWord(word)
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditWordDialog(word: com.codewithmehran.wordmaster.model.Word) {
        val editDialog = EditWordDialog(
            context = this,
            originalWord = word,
            onWordUpdated = { updatedWord ->
                wordViewModel.updateWord(updatedWord)
                wordViewModel.loadWord(currentWordId)
            }
        )
        editDialog.show()
    }
}