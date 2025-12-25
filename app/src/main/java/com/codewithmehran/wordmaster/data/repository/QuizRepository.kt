package com.codewithmehran.wordmaster.data.repository

import com.codewithmehran.wordmaster.data.dao.QuizDao
import com.codewithmehran.wordmaster.model.QuizResult

class QuizRepository(private val quizDao: QuizDao) {

    suspend fun saveResult(result: QuizResult): Long {
        return quizDao.insertResult(result)
    }

    suspend fun getRecentResults(limit: Int = 10): List<QuizResult> {
        return quizDao.getLatestResults(limit)
    }
}


