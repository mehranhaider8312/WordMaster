package com.codewithmehran.wordmaster.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.codewithmehran.wordmaster.data.repository.WordRepository
import com.codewithmehran.wordmaster.model.Word
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class WordViewModel(private val repository: WordRepository) : ViewModel() {

    private val _words = MutableLiveData<List<Word>>()
    val words: LiveData<List<Word>> = _words

    private val _selectedWord = MutableLiveData<Word?>()
    val selectedWord: LiveData<Word?> = _selectedWord

    fun loadWords() {
        viewModelScope.launch(Dispatchers.IO) {
            val allWords = repository.getAllWords()
            _words.postValue(allWords)
        }
    }

    fun loadWord(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val word = repository.getWordById(id)
            _selectedWord.postValue(word)
        }
    }

    fun addWord(
        wordText: String,
        meaning: String,
        synonyms: String,
        antonyms: String,
        exampleSentence: String,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val word = Word(
                word = wordText.trim(),
                meaning = meaning.trim(),
                synonyms = synonyms.trim(),
                antonyms = antonyms.trim(),
                exampleSentence = exampleSentence.trim(),
                dateAdded = Date()
            )
            repository.addWord(word)
            loadWords()
            onComplete()
        }
    }

    fun updateWord(
        existingId: Long,
        wordText: String,
        meaning: String,
        synonyms: String,
        antonyms: String,
        exampleSentence: String,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val updated = Word(
                id = existingId,
                word = wordText.trim(),
                meaning = meaning.trim(),
                synonyms = synonyms.trim(),
                antonyms = antonyms.trim(),
                exampleSentence = exampleSentence.trim(),
                dateAdded = Date()
            )
            repository.updateWord(updated)
            loadWords()
            onComplete()
        }
    }
}

class WordViewModelFactory(
    private val repository: WordRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WordViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


