package com.vocablink.data.repository

import com.vocablink.data.dao.WordDao
import com.vocablink.data.entity.Word
import kotlinx.coroutines.flow.Flow

class WordRepository(private val wordDao: WordDao) {
    val allWords: Flow<List<Word>> = wordDao.getAllWords()
    val favoriteWords: Flow<List<Word>> = wordDao.getFavoriteWords()
    val wordCount: Flow<Int> = wordDao.getWordCount()
    val totalCorrect: Flow<Int?> = wordDao.getTotalCorrect()
    val totalIncorrect: Flow<Int?> = wordDao.getTotalIncorrect()

    fun searchWords(query: String): Flow<List<Word>> = wordDao.searchWords(query)

    suspend fun getRandomWords(count: Int): List<Word> = wordDao.getRandomWords(count)

    suspend fun getWeakWords(count: Int): List<Word> = wordDao.getWeakWords(count)

    suspend fun insert(word: Word): Long = wordDao.insertWord(word)

    suspend fun insertAll(words: List<Word>) = wordDao.insertWords(words)

    suspend fun update(word: Word) = wordDao.updateWord(word)

    suspend fun delete(word: Word) = wordDao.deleteWord(word)

    suspend fun incrementCorrect(id: Long) = wordDao.incrementCorrect(id)

    suspend fun incrementIncorrect(id: Long) = wordDao.incrementIncorrect(id)

    suspend fun toggleFavorite(id: Long, isFavorite: Boolean) = wordDao.setFavorite(id, isFavorite)
}
