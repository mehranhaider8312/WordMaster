package com.codewithmehran.wordmaster.view.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.core.widget.doAfterTextChanged
import com.codewithmehran.wordmaster.databinding.EditWordDialogBinding
import com.codewithmehran.wordmaster.model.Word

class EditWordDialog(
    context: Context,
    private val originalWord: Word,
    private val onWordUpdated: (Word) -> Unit
) : Dialog(context) {

    private lateinit var binding: EditWordDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = EditWordDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set dialog to full screen
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Pre-fill fields with existing word data
        prefillFields()
        setupClickListeners()
        setupValidation()
    }

    private fun prefillFields() {
        binding.inputWord.setText(originalWord.word)
        binding.inputMeaning.setText(originalWord.meaning)
        binding.inputSynonyms.setText(originalWord.synonyms)
        binding.inputAntonyms.setText(originalWord.antonyms)
        binding.inputExample.setText(originalWord.exampleSentence)
    }

    private fun setupClickListeners() {
        binding.btnClear.setOnClickListener {
            clearAllFields()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnSave.setOnClickListener {
            if (validateInputs()) {
                val updatedWord = Word(
                    id = originalWord.id,
                    word = binding.inputWord.text.toString().trim(),
                    meaning = binding.inputMeaning.text.toString().trim(),
                    synonyms = binding.inputSynonyms.text.toString().trim(),
                    antonyms = binding.inputAntonyms.text.toString().trim(),
                    exampleSentence = binding.inputExample.text.toString().trim(),
                    dateAdded = originalWord.dateAdded // Keep original date
                )
                onWordUpdated(updatedWord)
                dismiss()
            }
        }
    }

    private fun setupValidation() {
        // Enable/disable save button based on input
        fun updateSaveButtonState() {
            val wordEmpty = binding.inputWord.text.isNullOrBlank()
            val meaningEmpty = binding.inputMeaning.text.isNullOrBlank()
            binding.btnSave.isEnabled = !wordEmpty && !meaningEmpty
        }

        binding.inputWord.doAfterTextChanged { updateSaveButtonState() }
        binding.inputMeaning.doAfterTextChanged { updateSaveButtonState() }
    }

    private fun validateInputs(): Boolean {
        val word = binding.inputWord.text.toString().trim()
        val meaning = binding.inputMeaning.text.toString().trim()

        if (word.isEmpty()) {
            binding.inputWord.error = "Word is required"
            return false
        }

        if (meaning.isEmpty()) {
            binding.inputMeaning.error = "Meaning is required"
            return false
        }

        return true
    }

    private fun clearAllFields() {
        binding.inputWord.text?.clear()
        binding.inputMeaning.text?.clear()
        binding.inputSynonyms.text?.clear()
        binding.inputAntonyms.text?.clear()
        binding.inputExample.text?.clear()
        binding.inputWord.requestFocus()
    }
}