package com.codewithmehran.wordmaster.model

import java.util.Date

data class Word(
    val id: Long = 0L,
    val word: String,
    val meaning: String,
    val synonyms: String,
    val antonyms: String,
    val exampleSentence: String,
    val dateAdded: Date
)


