package com.codewithmehran.wordmaster.model

import java.util.Date

data class Streak(
    val id: Long = 1L,
    val currentStreak: Int,
    val lastAddedDate: Date?
)


