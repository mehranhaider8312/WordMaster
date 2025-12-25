package com.codewithmehran.wordmaster.data.dao

import android.content.ContentValues
import android.content.Context
import com.codewithmehran.wordmaster.data.db.VocabularyDatabaseHelper
import com.codewithmehran.wordmaster.data.db.VocabularyTables
import com.codewithmehran.wordmaster.model.QuizResult
import java.util.Date

class QuizDao(context: Context) {

    private val dbHelper = VocabularyDatabaseHelper(context.applicationContext)

    fun insertResult(result: QuizResult): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(VocabularyTables.COL_SCORE, result.score)
            put(VocabularyTables.COL_TOTAL_QUESTIONS, result.totalQuestions)
            put(VocabularyTables.COL_QUIZ_DATE, result.date.time)
        }
        return db.insert(VocabularyTables.TABLE_QUIZ_RESULTS, null, values)
    }

    fun getLatestResults(limit: Int = 10): List<QuizResult> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ${VocabularyTables.TABLE_QUIZ_RESULTS} ORDER BY ${VocabularyTables.COL_QUIZ_DATE} DESC LIMIT ?",
            arrayOf(limit.toString())
        )
        val results = mutableListOf<QuizResult>()
        cursor.use {
            while (it.moveToNext()) {
                val id =
                    it.getLong(it.getColumnIndexOrThrow(VocabularyTables.COL_QUIZ_ID))
                val score =
                    it.getInt(it.getColumnIndexOrThrow(VocabularyTables.COL_SCORE))
                val total =
                    it.getInt(it.getColumnIndexOrThrow(VocabularyTables.COL_TOTAL_QUESTIONS))
                val dateMillis =
                    it.getLong(it.getColumnIndexOrThrow(VocabularyTables.COL_QUIZ_DATE))
                results.add(
                    QuizResult(
                        id = id,
                        score = score,
                        totalQuestions = total,
                        date = Date(dateMillis)
                    )
                )
            }
        }
        return results
    }
}


