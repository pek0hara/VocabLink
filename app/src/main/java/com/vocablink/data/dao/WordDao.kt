package com.vocablink.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.vocablink.data.entity.Word
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Query("SELECT * FROM words ORDER BY createdAt DESC")
    fun getAllWords(): Flow<List<Word>>

    @Query("SELECT * FROM words WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteWords(): Flow<List<Word>>

    @Query("SELECT * FROM words WHERE id = :id")
    suspend fun getWordById(id: Long): Word?

    @Query("SELECT * FROM words ORDER BY RANDOM() LIMIT :count")
    suspend fun getRandomWords(count: Int): List<Word>

    @Query("SELECT * FROM words ORDER BY correctCount * 1.0 / MAX(correctCount + incorrectCount, 1) ASC LIMIT :count")
    suspend fun getWeakWords(count: Int): List<Word>

    @Query("SELECT COUNT(*) FROM words")
    fun getWordCount(): Flow<Int>

    @Query("SELECT SUM(correctCount) FROM words")
    fun getTotalCorrect(): Flow<Int?>

    @Query("SELECT SUM(incorrectCount) FROM words")
    fun getTotalIncorrect(): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: Word): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWords(words: List<Word>)

    @Update
    suspend fun updateWord(word: Word)

    @Delete
    suspend fun deleteWord(word: Word)

    @Query("UPDATE words SET correctCount = correctCount + 1, lastStudied = :timestamp WHERE id = :id")
    suspend fun incrementCorrect(id: Long, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE words SET incorrectCount = incorrectCount + 1, lastStudied = :timestamp WHERE id = :id")
    suspend fun incrementIncorrect(id: Long, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE words SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: Long, isFavorite: Boolean)

    @Query("SELECT * FROM words WHERE english LIKE '%' || :query || '%' OR japanese LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchWords(query: String): Flow<List<Word>>
}
