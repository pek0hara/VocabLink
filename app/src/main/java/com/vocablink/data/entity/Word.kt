package com.vocablink.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class Word(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val english: String,
    val japanese: String,
    val example: String = "",
    val correctCount: Int = 0,
    val incorrectCount: Int = 0,
    val lastStudied: Long = 0,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    val totalAttempts: Int get() = correctCount + incorrectCount
    val accuracy: Float get() = if (totalAttempts > 0) correctCount.toFloat() / totalAttempts else 0f
}
