package com.codewithmehran.wordmaster.data.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.codewithmehran.wordmaster.data.db.VocabularyDatabaseHelper
import com.codewithmehran.wordmaster.data.db.VocabularyTables
import com.codewithmehran.wordmaster.model.Word
import java.util.Date

class WordDao(context: Context) {

    private val dbHelper = VocabularyDatabaseHelper(context.applicationContext)

    fun insert(word: Word): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(VocabularyTables.COL_WORD, word.word)
            put(VocabularyTables.COL_MEANING, word.meaning)
            put(VocabularyTables.COL_SYNONYMS, word.synonyms)
            put(VocabularyTables.COL_ANTONYMS, word.antonyms)
            put(VocabularyTables.COL_EXAMPLE, word.exampleSentence)
            put(VocabularyTables.COL_SOURCE, word.source)
            put(VocabularyTables.COL_DATE_ADDED, word.dateAdded.time)
        }
        return db.insert(VocabularyTables.TABLE_WORDS, null, values)
    }

    fun update(word: Word): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(VocabularyTables.COL_WORD, word.word)
            put(VocabularyTables.COL_MEANING, word.meaning)
            put(VocabularyTables.COL_SYNONYMS, word.synonyms)
            put(VocabularyTables.COL_ANTONYMS, word.antonyms)
            put(VocabularyTables.COL_EXAMPLE, word.exampleSentence)
            put(VocabularyTables.COL_SOURCE, word.source)
            put(VocabularyTables.COL_DATE_ADDED, word.dateAdded.time)
        }
        return db.update(
            VocabularyTables.TABLE_WORDS,
            values,
            "${VocabularyTables.COL_WORD_ID} = ?",
            arrayOf(word.id.toString())
        )
    }

    fun delete(word: Word): Int {
        val db = dbHelper.writableDatabase
        return db.delete(
            VocabularyTables.TABLE_WORDS,
            "${VocabularyTables.COL_WORD_ID} = ?",
            arrayOf(word.id.toString())
        )
    }

    fun getAllWords(): List<Word> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ${VocabularyTables.TABLE_WORDS} ORDER BY ${VocabularyTables.COL_DATE_ADDED} DESC",
            null
        )
        return cursor.use { mapWords(it) }
    }

    fun getWordById(id: Long): Word? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ${VocabularyTables.TABLE_WORDS} WHERE ${VocabularyTables.COL_WORD_ID} = ?",
            arrayOf(id.toString())
        )
        return cursor.use {
            if (it.moveToFirst()) mapWord(it) else null
        }
    }

    fun getRandomWords(limit: Int): List<Word> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ${VocabularyTables.TABLE_WORDS} ORDER BY RANDOM() LIMIT ?",
            arrayOf(limit.toString())
        )
        return cursor.use { mapWords(it) }
    }

    private fun mapWords(cursor: Cursor): List<Word> {
        val words = mutableListOf<Word>()
        while (cursor.moveToNext()) {
            words.add(mapWord(cursor))
        }
        return words
    }

    private fun mapWord(cursor: Cursor): Word {
        val id = cursor.getLong(cursor.getColumnIndexOrThrow(VocabularyTables.COL_WORD_ID))
        val word = cursor.getString(cursor.getColumnIndexOrThrow(VocabularyTables.COL_WORD))
        val meaning = cursor.getString(cursor.getColumnIndexOrThrow(VocabularyTables.COL_MEANING))
        val synonyms = cursor.getString(cursor.getColumnIndexOrThrow(VocabularyTables.COL_SYNONYMS))
        val antonyms = cursor.getString(cursor.getColumnIndexOrThrow(VocabularyTables.COL_ANTONYMS))
        val example = cursor.getString(cursor.getColumnIndexOrThrow(VocabularyTables.COL_EXAMPLE))
        val source = cursor.getString(cursor.getColumnIndexOrThrow(VocabularyTables.COL_SOURCE))
        val dateAddedMillis = cursor.getLong(cursor.getColumnIndexOrThrow(VocabularyTables.COL_DATE_ADDED))

        return Word(
            id = id,
            word = word,
            meaning = meaning,
            synonyms = synonyms,
            antonyms = antonyms,
            exampleSentence = example,
            source = source,
            dateAdded = Date(dateAddedMillis)
        )
    }
}