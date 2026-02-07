package com.vocablink.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vocablink.ui.theme.Correct
import com.vocablink.ui.theme.Incorrect
import com.vocablink.ui.theme.TextSecondary
import com.vocablink.viewmodel.QuizMode
import com.vocablink.viewmodel.QuizState
import com.vocablink.viewmodel.QuizViewModel

@Composable
fun QuizScreen(viewModel: QuizViewModel) {
    val quizState by viewModel.quizState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "クイズ",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        when {
            quizState.isFinished -> QuizResultContent(quizState, viewModel)
            quizState.currentWord != null -> QuizQuestionContent(quizState, viewModel)
            else -> QuizStartContent(viewModel)
        }
    }
}

@Composable
private fun QuizStartContent(viewModel: QuizViewModel) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "学習モードを選択",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            ElevatedButton(
                onClick = { viewModel.startQuiz(QuizMode.ENGLISH_TO_JAPANESE) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("英語 → 日本語", fontSize = 16.sp)
            }

            ElevatedButton(
                onClick = { viewModel.startQuiz(QuizMode.JAPANESE_TO_ENGLISH) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("日本語 → 英語", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = { viewModel.startQuiz(QuizMode.ENGLISH_TO_JAPANESE, useWeakWords = true) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("苦手な単語を復習", fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun QuizQuestionContent(state: QuizState, viewModel: QuizViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Progress
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "問題 ${state.questionNumber + 1} / ${state.totalQuestions}",
                fontSize = 14.sp,
                color = TextSecondary
            )
            Text(
                text = "${state.correctAnswers}問正解",
                fontSize = 14.sp,
                color = Correct
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { (state.questionNumber + 1).toFloat() / state.totalQuestions },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = MaterialTheme.colorScheme.primary,
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Question
        Text(
            text = when (state.quizMode) {
                QuizMode.ENGLISH_TO_JAPANESE -> "この英単語の意味は？"
                QuizMode.JAPANESE_TO_ENGLISH -> "この日本語に合う英語は？"
            },
            fontSize = 14.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            )
        ) {
            Text(
                text = when (state.quizMode) {
                    QuizMode.ENGLISH_TO_JAPANESE -> state.currentWord?.english ?: ""
                    QuizMode.JAPANESE_TO_ENGLISH -> state.currentWord?.japanese ?: ""
                },
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Options
        state.options.forEach { option ->
            val correctAnswer = when (state.quizMode) {
                QuizMode.ENGLISH_TO_JAPANESE -> state.currentWord?.japanese ?: ""
                QuizMode.JAPANESE_TO_ENGLISH -> state.currentWord?.english ?: ""
            }
            val isSelected = state.selectedAnswer == option
            val isCorrectOption = option == correctAnswer
            val hasAnswered = state.selectedAnswer != null

            val backgroundColor by animateColorAsState(
                targetValue = when {
                    hasAnswered && isCorrectOption -> Correct.copy(alpha = 0.15f)
                    hasAnswered && isSelected && !isCorrectOption -> Incorrect.copy(alpha = 0.15f)
                    else -> Color.Transparent
                },
                label = "optionBg"
            )

            val borderColor by animateColorAsState(
                targetValue = when {
                    hasAnswered && isCorrectOption -> Correct
                    hasAnswered && isSelected && !isCorrectOption -> Incorrect
                    else -> MaterialTheme.colorScheme.outline
                },
                label = "optionBorder"
            )

            OutlinedButton(
                onClick = { viewModel.selectAnswer(option) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(
                    width = if (hasAnswered && (isCorrectOption || isSelected)) 2.dp else 1.dp,
                    color = borderColor
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = backgroundColor
                ),
                enabled = !hasAnswered
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = option,
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (hasAnswered && isCorrectOption) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Correct,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Next button
        if (state.selectedAnswer != null) {
            Button(
                onClick = { viewModel.nextQuestion() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("次の問題", fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun QuizResultContent(state: QuizState, viewModel: QuizViewModel) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val percentage = if (state.totalQuestions > 0) {
                (state.correctAnswers.toFloat() / state.totalQuestions * 100).toInt()
            } else 0

            Text(
                text = when {
                    percentage >= 90 -> "素晴らしい！"
                    percentage >= 70 -> "よくできました！"
                    percentage >= 50 -> "まあまあ！"
                    else -> "もっと頑張ろう！"
                },
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "$percentage%",
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                color = if (percentage >= 70) Correct else Incorrect
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${state.totalQuestions}問中 ${state.correctAnswers}問正解",
                fontSize = 18.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.startQuiz(state.quizMode) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("もう一度", fontSize = 16.sp)
            }
        }
    }
}
