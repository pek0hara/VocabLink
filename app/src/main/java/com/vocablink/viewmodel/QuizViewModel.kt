package com.vocablink.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vocablink.data.entity.Word
import com.vocablink.data.repository.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class QuizMode { ENGLISH_TO_JAPANESE, JAPANESE_TO_ENGLISH }

data class QuizState(
    val currentWord: Word? = null,
    val options: List<String> = emptyList(),
    val selectedAnswer: String? = null,
    val isCorrect: Boolean? = null,
    val questionNumber: Int = 0,
    val totalQuestions: Int = 10,
    val correctAnswers: Int = 0,
    val isFinished: Boolean = false,
    val quizMode: QuizMode = QuizMode.ENGLISH_TO_JAPANESE
)

class QuizViewModel(private val repository: WordRepository) : ViewModel() {

    private val _quizState = MutableStateFlow(QuizState())
    val quizState: StateFlow<QuizState> = _quizState.asStateFlow()

    private var quizWords: List<Word> = emptyList()
    private var allQuizWords: List<Word> = emptyList()

    fun startQuiz(mode: QuizMode = QuizMode.ENGLISH_TO_JAPANESE, useWeakWords: Boolean = false) {
        viewModelScope.launch {
            val words = if (useWeakWords) {
                repository.getWeakWords(20)
            } else {
                repository.getRandomWords(20)
            }
            if (words.size < 4) return@launch

            allQuizWords = words
            quizWords = words.shuffled().take(10)
            _quizState.value = QuizState(
                totalQuestions = quizWords.size.coerceAtMost(10),
                quizMode = mode
            )
            loadNextQuestion()
        }
    }

    private fun loadNextQuestion() {
        val state = _quizState.value
        if (state.questionNumber >= state.totalQuestions) {
            _quizState.value = state.copy(isFinished = true)
            return
        }

        val currentWord = quizWords[state.questionNumber]
        val correctAnswer = when (state.quizMode) {
            QuizMode.ENGLISH_TO_JAPANESE -> currentWord.japanese
            QuizMode.JAPANESE_TO_ENGLISH -> currentWord.english
        }

        val wrongAnswers = allQuizWords
            .filter { it.id != currentWord.id }
            .shuffled()
            .take(3)
            .map { word ->
                when (state.quizMode) {
                    QuizMode.ENGLISH_TO_JAPANESE -> word.japanese
                    QuizMode.JAPANESE_TO_ENGLISH -> word.english
                }
            }

        val options = (wrongAnswers + correctAnswer).shuffled()

        _quizState.value = state.copy(
            currentWord = currentWord,
            options = options,
            selectedAnswer = null,
            isCorrect = null
        )
    }

    fun selectAnswer(answer: String) {
        val state = _quizState.value
        val currentWord = state.currentWord ?: return
        if (state.selectedAnswer != null) return

        val correctAnswer = when (state.quizMode) {
            QuizMode.ENGLISH_TO_JAPANESE -> currentWord.japanese
            QuizMode.JAPANESE_TO_ENGLISH -> currentWord.english
        }

        val isCorrect = answer == correctAnswer

        viewModelScope.launch {
            if (isCorrect) {
                repository.incrementCorrect(currentWord.id)
            } else {
                repository.incrementIncorrect(currentWord.id)
            }
        }

        _quizState.value = state.copy(
            selectedAnswer = answer,
            isCorrect = isCorrect,
            correctAnswers = if (isCorrect) state.correctAnswers + 1 else state.correctAnswers
        )
    }

    fun nextQuestion() {
        val state = _quizState.value
        _quizState.value = state.copy(questionNumber = state.questionNumber + 1)
        loadNextQuestion()
    }

    class Factory(private val repository: WordRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
                return QuizViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
