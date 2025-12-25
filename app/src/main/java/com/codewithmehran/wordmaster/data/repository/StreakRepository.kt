package com.codewithmehran.wordmaster.data.repository

import com.codewithmehran.wordmaster.data.dao.StreakDao
import com.codewithmehran.wordmaster.model.Streak

class StreakRepository(private val streakDao: StreakDao) {

    suspend fun getStreak(): Streak {
        return streakDao.getStreak()
    }

    suspend fun updateStreak(streak: Streak) {
        streakDao.updateStreak(streak)
    }
}


