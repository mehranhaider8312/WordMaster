package com.codewithmehran.wordmaster.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

private const val DATABASE_NAME = "vocabulary.db"
private const val DATABASE_VERSION = 3

object VocabularyTables {
    const val TABLE_WORDS = "words"
    const val COL_WORD_ID = "id"
    const val COL_WORD = "word"
    const val COL_MEANING = "meaning"
    const val COL_SYNONYMS = "synonyms"
    const val COL_ANTONYMS = "antonyms"
    const val COL_EXAMPLE = "example_sentence"
    const val COL_SOURCE = "source"
    const val COL_DATE_ADDED = "dateAdded"

    const val TABLE_STREAK = "streak"
    const val COL_STREAK_ID = "id"
    const val COL_CURRENT_STREAK = "currentStreak"
    const val COL_LAST_ADDED_DATE = "lastAddedDate"

    const val TABLE_QUIZ_RESULTS = "quiz_results"
    const val COL_QUIZ_ID = "id"
    const val COL_SCORE = "score"
    const val COL_TOTAL_QUESTIONS = "totalQuestions"
    const val COL_QUIZ_DATE = "date"
}

class VocabularyDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE ${VocabularyTables.TABLE_WORDS} (
                ${VocabularyTables.COL_WORD_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${VocabularyTables.COL_WORD} TEXT NOT NULL,
                ${VocabularyTables.COL_MEANING} TEXT NOT NULL,
                ${VocabularyTables.COL_SYNONYMS} TEXT NOT NULL,
                ${VocabularyTables.COL_ANTONYMS} TEXT NOT NULL,
                ${VocabularyTables.COL_EXAMPLE} TEXT NOT NULL,
                ${VocabularyTables.COL_SOURCE} TEXT NOT NULL DEFAULT '',
                ${VocabularyTables.COL_DATE_ADDED} INTEGER NOT NULL
            )
            """.trimIndent()
        )

        // Insert permanent system word
        val systemWordValues = android.content.ContentValues().apply {
            put(VocabularyTables.COL_WORD, "SYSTEM_WORD")
            put(VocabularyTables.COL_MEANING, "SYSTEM_WORD")
            put(VocabularyTables.COL_SYNONYMS, "SYSTEM_WORD")
            put(VocabularyTables.COL_ANTONYMS, "SYSTEM_WORD")
            put(VocabularyTables.COL_EXAMPLE, "SYSTEM_WORD")
            put(VocabularyTables.COL_SOURCE, "SYSTEM")
            put(VocabularyTables.COL_DATE_ADDED, Long.MAX_VALUE)
        }
        db.insert(VocabularyTables.TABLE_WORDS, null, systemWordValues)

        db.execSQL(
            """
            CREATE TABLE ${VocabularyTables.TABLE_STREAK} (
                ${VocabularyTables.COL_STREAK_ID} INTEGER PRIMARY KEY,
                ${VocabularyTables.COL_CURRENT_STREAK} INTEGER NOT NULL DEFAULT 0,
                ${VocabularyTables.COL_LAST_ADDED_DATE} INTEGER
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            INSERT INTO ${VocabularyTables.TABLE_STREAK} 
            (${VocabularyTables.COL_STREAK_ID}, ${VocabularyTables.COL_CURRENT_STREAK}, ${VocabularyTables.COL_LAST_ADDED_DATE})
            VALUES (1, 0, NULL)
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE ${VocabularyTables.TABLE_QUIZ_RESULTS} (
                ${VocabularyTables.COL_QUIZ_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${VocabularyTables.COL_SCORE} INTEGER NOT NULL,
                ${VocabularyTables.COL_TOTAL_QUESTIONS} INTEGER NOT NULL,
                ${VocabularyTables.COL_QUIZ_DATE} INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE ${VocabularyTables.TABLE_WORDS} ADD COLUMN ${VocabularyTables.COL_SOURCE} TEXT NOT NULL DEFAULT ''")
        }
        
        if (oldVersion < 3) {
            val systemWordValues = android.content.ContentValues().apply {
                put(VocabularyTables.COL_WORD, "SYSTEM_WORD")
                put(VocabularyTables.COL_MEANING, "SYSTEM_WORD")
                put(VocabularyTables.COL_SYNONYMS, "SYSTEM_WORD")
                put(VocabularyTables.COL_ANTONYMS, "SYSTEM_WORD")
                put(VocabularyTables.COL_EXAMPLE, "SYSTEM_WORD")
                put(VocabularyTables.COL_SOURCE, "SYSTEM")
                put(VocabularyTables.COL_DATE_ADDED, Long.MAX_VALUE)
            }
            db.insert(VocabularyTables.TABLE_WORDS, null, systemWordValues)
        }
    }
}