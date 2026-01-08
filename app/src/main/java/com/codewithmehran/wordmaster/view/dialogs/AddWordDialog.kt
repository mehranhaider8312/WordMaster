package com.codewithmehran.wordmaster.view.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.codewithmehran.wordmaster.R
import com.codewithmehran.wordmaster.databinding.AddWordDialogBinding

class AddWordDialog(
    context: Context,
    private val onSaveWord: (String, String, String, String, String, String) -> Unit
) : Dialog(context) {

    private lateinit var binding: AddWordDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = AddWordDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )

        window?.setDimAmount(0.8f)

        window?.setBackgroundDrawableResource(android.R.color.transparent)

        setupUI()
        setupAnimations()
        setupClickListeners()
    }

    private fun setupUI() {
        binding.btnClear.setOnClickListener {
            binding.inputWord.text?.clear()
            binding.inputMeaning.text?.clear()
            binding.inputSynonyms.text?.clear()
            binding.inputAntonyms.text?.clear()
            binding.inputExample.text?.clear()
            binding.chipGroupSource.clearCheck()

            Toast.makeText(context, "All fields cleared", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupAnimations() {
        val fadeIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in)
        val slideUp = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left)

        binding.dialogContainer.startAnimation(fadeIn)
        binding.headerContainer.startAnimation(slideUp)
        binding.inputContainer.startAnimation(slideUp)
        binding.buttonsContainer.startAnimation(slideUp)
    }

    private fun setupClickListeners() {
        binding.btnSave.setOnClickListener {
            val word = binding.inputWord.text.toString().trim()
            val meaning = binding.inputMeaning.text.toString().trim()
            val synonyms = binding.inputSynonyms.text.toString().trim()
            val antonyms = binding.inputAntonyms.text.toString().trim()
            val example = binding.inputExample.text.toString().trim()
            val source = getSelectedSource()

            if (validateInputs(word, meaning, synonyms, antonyms)) {
                val scaleAnimation = AnimationUtils.loadAnimation(context, R.anim.scale_in)
                binding.btnSave.startAnimation(scaleAnimation)

                onSaveWord(word, meaning, synonyms, antonyms, example, source)

                dismissWithAnimation()

                Toast.makeText(context, "Word added successfully!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnCancel.setOnClickListener {
            dismissWithAnimation()
        }

        binding.root.setOnClickListener {
            dismissWithAnimation()
        }

        binding.dialogContainer.setOnClickListener {
        }
    }

    private fun getSelectedSource(): String {
        return when (binding.chipGroupSource.checkedChipId) {
            R.id.chipBook -> "Book"
            R.id.chipArticle -> "Article"
            R.id.chipYoutube -> "YouTube"
            R.id.chipConversation -> "Conversation"
            R.id.chipOther -> "Other"
            else -> ""
        }
    }

    private fun validateInputs(
        word: String,
        meaning: String,
        synonyms: String,
        antonyms: String
    ): Boolean {
        if (word.isEmpty()) {
            binding.inputWord.error = "Word is required"
            binding.inputWord.requestFocus()
            return false
        }

        if (meaning.isEmpty()) {
            binding.inputMeaning.error = "Meaning is required"
            binding.inputMeaning.requestFocus()
            return false
        }

        if (synonyms.isEmpty()) {
            binding.inputSynonyms.error = "Synonyms are required"
            binding.inputSynonyms.requestFocus()
            return false
        }

        if (antonyms.isEmpty()) {
            binding.inputAntonyms.error = "Antonyms are required"
            binding.inputAntonyms.requestFocus()
            return false
        }

        return true
    }

    private fun dismissWithAnimation() {
        val fadeOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out)
        binding.dialogContainer.startAnimation(fadeOut)

        fadeOut.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
            override fun onAnimationStart(animation: android.view.animation.Animation?) {}

            override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                dismiss()
            }

            override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
        })
    }

    override fun show() {
        super.show()
        val slideIn = AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom)
        binding.dialogContainer.startAnimation(slideIn)
    }
}