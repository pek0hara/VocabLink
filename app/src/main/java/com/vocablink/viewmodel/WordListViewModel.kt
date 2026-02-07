package com.vocablink.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vocablink.data.SampleData
import com.vocablink.data.entity.Word
import com.vocablink.data.repository.WordRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WordListViewModel(private val repository: WordRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _showFavoritesOnly = MutableStateFlow(false)
    val showFavoritesOnly: StateFlow<Boolean> = _showFavoritesOnly.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val words: StateFlow<List<Word>> = _searchQuery.flatMapLatest { query ->
        if (query.isBlank()) {
            if (_showFavoritesOnly.value) repository.favoriteWords
            else repository.allWords
        } else {
            repository.searchWords(query)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun toggleFavoritesFilter() {
        _showFavoritesOnly.value = !_showFavoritesOnly.value
        _searchQuery.value = _searchQuery.value // trigger re-collect
    }

    fun addWord(english: String, japanese: String, example: String) {
        viewModelScope.launch {
            repository.insert(Word(english = english, japanese = japanese, example = example))
        }
    }

    fun deleteWord(word: Word) {
        viewModelScope.launch {
            repository.delete(word)
        }
    }

    fun toggleFavorite(word: Word) {
        viewModelScope.launch {
            repository.toggleFavorite(word.id, !word.isFavorite)
        }
    }

    fun loadSampleData() {
        viewModelScope.launch {
            repository.insertAll(SampleData.words)
        }
    }

    class Factory(private val repository: WordRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WordListViewModel::class.java)) {
                return WordListViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
