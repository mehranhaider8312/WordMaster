package com.codewithmehran.wordmaster.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.codewithmehran.wordmaster.data.repository.QuizRepository
import com.codewithmehran.wordmaster.data.repository.WordRepository
import com.codewithmehran.wordmaster.model.QuizResult
import com.codewithmehran.wordmaster.model.Word
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

data class QuizQuestion(
    val word: Word,
    val options: List<String>,
    val correctIndex: Int
)

class QuizViewModel(
    private val wordRepository: WordRepository,
    private val quizRepository: QuizRepository
) : ViewModel() {

    private val _questions = MutableLiveData<List<QuizQuestion>>()
    val questions: LiveData<List<QuizQuestion>> = _questions

    private val _results = MutableLiveData<List<QuizResult>>()
    val results: LiveData<List<QuizResult>> = _results

    fun generateQuiz(questionCount: Int = 5) {
        viewModelScope.launch(Dispatchers.IO) {
            val words = wordRepository.getRandomWords(questionCount * 2)
            val questions = words.take(questionCount).map { word ->
                val distractors = words
                    .filter { it.id != word.id }
                    .shuffled()
                    .take(3)
                    .map { it.meaning }

                val allOptions = (distractors + word.meaning).shuffled()
                val correctIndex = allOptions.indexOf(word.meaning)

                QuizQuestion(
                    word = word,
                    options = allOptions,
                    correctIndex = correctIndex
                )
            }
            _questions.postValue(questions)
        }
    }

    fun saveResult(score: Int, totalQuestions: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = QuizResult(
                score = score,
                totalQuestions = totalQuestions,
                date = Date()
            )
            quizRepository.saveResult(result)
            val recent = quizRepository.getRecentResults()
            _results.postValue(recent)
        }
    }
}

class QuizViewModelFactory(
    private val wordRepository: WordRepository,
    private val quizRepository: QuizRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QuizViewModel(wordRepository, quizRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


