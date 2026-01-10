package com.codewithmehran.wordmaster.view.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.codewithmehran.wordmaster.utils.MixpanelHelper
import com.codewithmehran.wordmaster.R

class QuizDialog(
    context: Context,
    private val question: String,
    private val option1: String,
    private val option2: String,
    private val option3: String,
    private val option4: String,
    private val correctAnswer: Int, // 1, 2, 3, or 4
    private val onDismiss: (() -> Unit)? = null
) : Dialog(context) {

    private lateinit var tvQuestion: TextView
    private lateinit var tvOption1: TextView
    private lateinit var tvOption2: TextView
    private lateinit var tvOption3: TextView
    private lateinit var tvOption4: TextView

    private lateinit var cardOption1: CardView
    private lateinit var cardOption2: CardView
    private lateinit var cardOption3: CardView
    private lateinit var cardOption4: CardView

    private lateinit var ivCheckOption1: ImageView
    private lateinit var ivCheckOption2: ImageView
    private lateinit var ivCheckOption3: ImageView
    private lateinit var ivCheckOption4: ImageView

    private lateinit var cardResult: CardView
    private lateinit var ivResultIcon: ImageView
    private lateinit var tvResultTitle: TextView
    private lateinit var tvResultMessage: TextView

    private lateinit var btnClose: ImageView

    private var selectedOption: Int = 0
    private var isAnswered = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.quiz_dialog_layout)

        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setLayout(
            (context.resources.displayMetrics.widthPixels * 0.9).toInt(),
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )

        initViews()
        setupData()
        setupListeners()
    }

    private fun initViews() {
        tvQuestion = findViewById(R.id.tvQuestion)

        tvOption1 = findViewById(R.id.tvOption1)
        tvOption2 = findViewById(R.id.tvOption2)
        tvOption3 = findViewById(R.id.tvOption3)
        tvOption4 = findViewById(R.id.tvOption4)

        cardOption1 = findViewById(R.id.cardOption1)
        cardOption2 = findViewById(R.id.cardOption2)
        cardOption3 = findViewById(R.id.cardOption3)
        cardOption4 = findViewById(R.id.cardOption4)

        ivCheckOption1 = findViewById(R.id.ivCheckOption1)
        ivCheckOption2 = findViewById(R.id.ivCheckOption2)
        ivCheckOption3 = findViewById(R.id.ivCheckOption3)
        ivCheckOption4 = findViewById(R.id.ivCheckOption4)

        cardResult = findViewById(R.id.cardResult)
        ivResultIcon = findViewById(R.id.ivResultIcon)
        tvResultTitle = findViewById(R.id.tvResultTitle)
        tvResultMessage = findViewById(R.id.tvResultMessage)

        btnClose = findViewById(R.id.btnClose)
    }

    private fun setupData() {
        tvQuestion.text = question
        tvOption1.text = option1
        tvOption2.text = option2
        tvOption3.text = option3
        tvOption4.text = option4
    }

    private fun setupListeners() {
        btnClose.setOnClickListener {
            dismiss()
            onDismiss?.invoke()
        }

        cardOption1.setOnClickListener { selectOption(1) }
        cardOption2.setOnClickListener { selectOption(2) }
        cardOption3.setOnClickListener { selectOption(3) }
        cardOption4.setOnClickListener { selectOption(4) }
    }

    private fun selectOption(option: Int) {
        if (isAnswered) return

        selectedOption = option
        isAnswered = true

        // Reset all options
        resetAllOptions()

        // Highlight selected option
        when (option) {
            1 -> {
                highlightOption(cardOption1, ivCheckOption1, option == correctAnswer)
            }
            2 -> {
                highlightOption(cardOption2, ivCheckOption2, option == correctAnswer)
            }
            3 -> {
                highlightOption(cardOption3, ivCheckOption3, option == correctAnswer)
            }
            4 -> {
                highlightOption(cardOption4, ivCheckOption4, option == correctAnswer)
            }
        }

        // Show the correct answer if user selected wrong
        if (option != correctAnswer) {
            when (correctAnswer) {
                1 -> highlightCorrectOption(cardOption1, ivCheckOption1)
                2 -> highlightCorrectOption(cardOption2, ivCheckOption2)
                3 -> highlightCorrectOption(cardOption3, ivCheckOption3)
                4 -> highlightCorrectOption(cardOption4, ivCheckOption4)
            }
        }

        // Show result card
        showResult(option == correctAnswer)

        MixpanelHelper.trackQuizTaken(question, option == correctAnswer)
    }

    private fun resetAllOptions() {
        cardOption1.background = ContextCompat.getDrawable(context, R.drawable.bg_option_default)
        cardOption2.background = ContextCompat.getDrawable(context, R.drawable.bg_option_default)
        cardOption3.background = ContextCompat.getDrawable(context, R.drawable.bg_option_default)
        cardOption4.background = ContextCompat.getDrawable(context, R.drawable.bg_option_default)

        ivCheckOption1.visibility = View.GONE
        ivCheckOption2.visibility = View.GONE
        ivCheckOption3.visibility = View.GONE
        ivCheckOption4.visibility = View.GONE
    }

    private fun highlightOption(card: CardView, checkIcon: ImageView, isCorrect: Boolean) {
        if (isCorrect) {
            card.background = ContextCompat.getDrawable(context, R.drawable.bg_option_correct)
            checkIcon.setColorFilter(ContextCompat.getColor(context, R.color.icon_correct))
            checkIcon.setImageResource(R.drawable.ic_check)
        } else {
            card.background = ContextCompat.getDrawable(context, R.drawable.bg_option_wrong)
            checkIcon.setImageResource(R.drawable.ic_clear)
            checkIcon.setColorFilter(ContextCompat.getColor(context, R.color.icon_wrong))
        }
        checkIcon.visibility = View.VISIBLE
    }

    private fun highlightCorrectOption(card: CardView, checkIcon: ImageView) {
        card.background = ContextCompat.getDrawable(context, R.drawable.bg_option_correct)
        checkIcon.setImageResource(R.drawable.ic_check)
        checkIcon.setColorFilter(ContextCompat.getColor(context, R.color.icon_correct))
        checkIcon.visibility = View.VISIBLE
    }

    private fun showResult(isCorrect: Boolean) {
        cardResult.visibility = View.VISIBLE

        if (isCorrect) {
            cardResult.setCardBackgroundColor(ContextCompat.getColor(context, R.color.result_correct_bg))
            ivResultIcon.setImageResource(R.drawable.ic_check)
            ivResultIcon.setColorFilter(ContextCompat.getColor(context, R.color.result_correct_icon))
            tvResultTitle.text = "Correct!"
            tvResultTitle.setTextColor(ContextCompat.getColor(context, R.color.result_correct_text))
        } else {
            cardResult.setCardBackgroundColor(ContextCompat.getColor(context, R.color.result_wrong_bg))
            ivResultIcon.setImageResource(R.drawable.ic_clear)
            ivResultIcon.setColorFilter(ContextCompat.getColor(context, R.color.result_wrong_icon))
            tvResultTitle.text = "Incorrect"
            tvResultTitle.setTextColor(ContextCompat.getColor(context, R.color.result_wrong_text))
        }

        val correctAnswerText = when (correctAnswer) {
            1 -> option1
            2 -> option2
            3 -> option3
            4 -> option4
            else -> ""
        }
        tvResultMessage.text = "Correct answer: $correctAnswerText"
    }
}