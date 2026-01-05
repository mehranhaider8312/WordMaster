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

    private val _filteredWords = MutableLiveData<List<Word>>()
    val filteredWords: LiveData<List<Word>> = _filteredWords

    private val _selectedWord = MutableLiveData<Word?>()
    val selectedWord: LiveData<Word?> = _selectedWord

    init {
        // Initialize filteredWords with empty list
        _filteredWords.value = emptyList()
    }

    fun loadWords() {
        viewModelScope.launch(Dispatchers.IO) {
            val allWords = repository.getAllWords()
            _words.postValue(allWords)
            _filteredWords.postValue(allWords) // Also update filtered words
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

    // New method for filtering words based on search query
    fun filterWords(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentList = _words.value ?: emptyList()

            if (query.isEmpty()) {
                _filteredWords.postValue(currentList)
            } else {
                val filtered = currentList.filter { word ->
                    word.word.contains(query, ignoreCase = true) ||
                            word.meaning.contains(query, ignoreCase = true) ||
                            word.synonyms.contains(query, ignoreCase = true) ||
                            word.antonyms.contains(query, ignoreCase = true) ||
                            word.exampleSentence.contains(query, ignoreCase = true)
                }
                _filteredWords.postValue(filtered)
            }
        }
    }

    // New method to delete a word
    fun deleteWord(word: Word) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteWord(word)
            loadWords()
        }
    }

    // New method to update word object directly (for adapter)
    fun updateWord(word: Word) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateWord(word)
            loadWords()
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