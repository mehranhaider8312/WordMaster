package com.codewithmehran.wordmaster.view.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.codewithmehran.wordmaster.R
import com.codewithmehran.wordmaster.model.WordMasterApp
import com.codewithmehran.wordmaster.databinding.ActivityQuizBinding
import com.codewithmehran.wordmaster.viewmodel.QuizViewModel
import com.codewithmehran.wordmaster.viewmodel.QuizViewModelFactory

class QuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuizBinding

    private val quizViewModel: QuizViewModel by viewModels {
        val app = application as WordMasterApp
        QuizViewModelFactory(app.wordRepository, app.quizRepository)
    }

    private var currentIndex = 0
    private var score = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUi()

        quizViewModel.questions.observe(this) { questions ->
            if (questions.isNotEmpty()) {
                showQuestion(0)
            }
        }

        quizViewModel.results.observe(this) { results ->
            if (results.isNotEmpty()) {
                val latest = results.first()
                binding.txtScore.text = getString(
                    R.string.quiz_score,
                    latest.score,
                    latest.totalQuestions
                )
            }
        }
    }

    private fun setupUi() {
        binding.toolbar.title = getString(R.string.title_quiz)
        binding.toolbar.setNavigationIcon(R.drawable.arrow_back)
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.btnStart.setOnClickListener {
            currentIndex = 0
            score = 0
            quizViewModel.generateQuiz()
        }

        binding.btnNext.setOnClickListener {
            val questions = quizViewModel.questions.value.orEmpty()
            if (questions.isEmpty()) return@setOnClickListener

            val selected = binding.radioGroup.checkedRadioButtonId
            val userIndex = when (selected) {
                binding.option1.id -> 0
                binding.option2.id -> 1
                binding.option3.id -> 2
                binding.option4.id -> 3
                else -> -1
            }

            if (userIndex == questions[currentIndex].correctIndex) {
                score++
            }

            if (currentIndex < questions.size - 1) {
                currentIndex++
                showQuestion(currentIndex)
            } else {
                quizViewModel.saveResult(score, questions.size)
            }
        }
    }

    private fun showQuestion(index: Int) {
        val questions = quizViewModel.questions.value.orEmpty()
        if (index >= questions.size) return
        val question = questions[index]

        binding.txtWord.text = question.word.word
        binding.txtPrompt.text = getString(R.string.quiz_question)

        binding.option1.text = question.options.getOrNull(0)
        binding.option2.text = question.options.getOrNull(1)
        binding.option3.text = question.options.getOrNull(2)
        binding.option4.text = question.options.getOrNull(3)

        binding.radioGroup.clearCheck()
    }
}


