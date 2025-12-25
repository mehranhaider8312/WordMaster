package com.codewithmehran.wordmaster.data.dao

import android.content.ContentValues
import android.content.Context
import com.codewithmehran.wordmaster.data.db.VocabularyDatabaseHelper
import com.codewithmehran.wordmaster.data.db.VocabularyTables
import com.codewithmehran.wordmaster.model.Streak
import java.util.Date

class StreakDao(context: Context) {

    private val dbHelper = VocabularyDatabaseHelper(context.applicationContext)

    fun getStreak(): Streak {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ${VocabularyTables.TABLE_STREAK} WHERE ${VocabularyTables.COL_STREAK_ID} = 1",
            null
        )
        cursor.use {
            if (it.moveToFirst()) {
                val current =
                    it.getInt(it.getColumnIndexOrThrow(VocabularyTables.COL_CURRENT_STREAK))
                val lastDateMillis =
                    if (!it.isNull(it.getColumnIndexOrThrow(VocabularyTables.COL_LAST_ADDED_DATE))) {
                        it.getLong(it.getColumnIndexOrThrow(VocabularyTables.COL_LAST_ADDED_DATE))
                    } else {
                        null
                    }
                return Streak(
                    id = 1L,
                    currentStreak = current,
                    lastAddedDate = lastDateMillis?.let { millis -> Date(millis) }
                )
            }
        }
        return Streak(id = 1L, currentStreak = 0, lastAddedDate = null)
    }

    fun updateStreak(streak: Streak) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(VocabularyTables.COL_CURRENT_STREAK, streak.currentStreak)
            put(
                VocabularyTables.COL_LAST_ADDED_DATE,
                streak.lastAddedDate?.time
            )
        }
        db.update(
            VocabularyTables.TABLE_STREAK,
            values,
            "${VocabularyTables.COL_STREAK_ID} = 1",
            null
        )
    }
}


