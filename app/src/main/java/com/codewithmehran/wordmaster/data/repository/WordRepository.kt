package com.codewithmehran.wordmaster.data.repository

import com.codewithmehran.wordmaster.data.dao.WordDao
import com.codewithmehran.wordmaster.model.Word

class WordRepository(private val wordDao: WordDao) {

    suspend fun addWord(word: Word): Long {
        return wordDao.insert(word)
    }

    suspend fun updateWord(word: Word): Int {
        return wordDao.update(word)
    }

    suspend fun deleteWord(word: Word): Int {
        return wordDao.delete(word)
    }

    suspend fun getAllWords(): List<Word> {
        return wordDao.getAllWords()
    }

    suspend fun getRandomWords(limit: Int): List<Word> {
        return wordDao.getRandomWords(limit)
    }

    suspend fun getWordById(id: Long): Word? {
        return wordDao.getWordById(id)
    }
}