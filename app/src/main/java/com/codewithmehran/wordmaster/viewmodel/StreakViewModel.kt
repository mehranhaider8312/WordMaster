package com.codewithmehran.wordmaster.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.codewithmehran.wordmaster.data.repository.StreakRepository
import com.codewithmehran.wordmaster.model.Streak
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class StreakViewModel(private val repository: StreakRepository) : ViewModel() {

    private val _streak = MutableLiveData<Streak>()
    val streak: LiveData<Streak> = _streak

    fun loadStreak() {
        viewModelScope.launch(Dispatchers.IO) {
            val value = repository.getStreak()
            _streak.postValue(value)
        }
    }

    fun onWordAdded() {
        viewModelScope.launch(Dispatchers.IO) {
            val current = repository.getStreak()
            val today = startOfDay(Date())
            val lastDate = current.lastAddedDate?.let { startOfDay(it) }

            val updatedStreak = when {
                lastDate == null -> Streak(id = 1L, currentStreak = 1, lastAddedDate = today)
                isSameDay(lastDate, today) -> current // already counted today
                isYesterday(lastDate, today) -> current.copy(
                    currentStreak = current.currentStreak + 1,
                    lastAddedDate = today
                )
                else -> Streak(id = 1L, currentStreak = 1, lastAddedDate = today)
            }

            repository.updateStreak(updatedStreak)
            _streak.postValue(updatedStreak)
        }
    }

    private fun startOfDay(date: Date): Date {
        val cal = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.time
    }

    private fun isSameDay(a: Date, b: Date): Boolean {
        return a.time == b.time
    }

    private fun isYesterday(yesterday: Date, today: Date): Boolean {
        val cal = Calendar.getInstance().apply { time = today }
        cal.add(Calendar.DAY_OF_YEAR, -1)
        val expectedYesterday = startOfDay(cal.time)
        return yesterday.time == expectedYesterday.time
    }
}

class StreakViewModelFactory(
    private val repository: StreakRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StreakViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StreakViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


