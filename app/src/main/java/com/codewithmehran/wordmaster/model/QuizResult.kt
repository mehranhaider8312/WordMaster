package com.codewithmehran.wordmaster.model

import java.util.Date

data class QuizResult(
    val id: Long = 0L,
    val score: Int,
    val totalQuestions: Int,
    val date: Date
)


