package com.vocablink.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vocablink.data.repository.WordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StatsViewModel(repository: WordRepository) : ViewModel() {

    val wordCount: Flow<Int> = repository.wordCount

    val totalCorrect: Flow<Int> = repository.totalCorrect.map { it ?: 0 }

    val totalIncorrect: Flow<Int> = repository.totalIncorrect.map { it ?: 0 }

    val accuracy: Flow<Float> = repository.totalCorrect.map { correct ->
        val c = correct ?: 0
        c.toFloat()
    }.let { correctFlow ->
        repository.totalIncorrect.map { incorrect ->
            val i = incorrect ?: 0
            i
        }.let { incorrectFlow ->
            correctFlow.map { it }
        }
    }

    val allWords = repository.allWords

    class Factory(private val repository: WordRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(StatsViewModel::class.java)) {
                return StatsViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
