package com.vocablink.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vocablink.data.entity.Word
import com.vocablink.ui.theme.Correct
import com.vocablink.ui.theme.Incorrect
import com.vocablink.ui.theme.TextSecondary
import com.vocablink.viewmodel.StatsViewModel

@Composable
fun StatsScreen(viewModel: StatsViewModel) {
    val wordCount by viewModel.wordCount.collectAsState(initial = 0)
    val totalCorrect by viewModel.totalCorrect.collectAsState(initial = 0)
    val totalIncorrect by viewModel.totalIncorrect.collectAsState(initial = 0)
    val allWords by viewModel.allWords.collectAsState(initial = emptyList())

    val totalAttempts = totalCorrect + totalIncorrect
    val accuracy = if (totalAttempts > 0) totalCorrect.toFloat() / totalAttempts else 0f

    val studiedWords = allWords.filter { it.totalAttempts > 0 }
    val weakWords = studiedWords.filter { it.accuracy < 0.5f }.sortedBy { it.accuracy }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "学習統計",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Stats cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "登録単語数",
                    value = "$wordCount",
                    icon = Icons.Default.List,
                    iconTint = MaterialTheme.colorScheme.primary
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "学習済み",
                    value = "${studiedWords.size}",
                    icon = Icons.Default.Star,
                    iconTint = Correct
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "正解数",
                    value = "$totalCorrect",
                    icon = Icons.Default.CheckCircle,
                    iconTint = Correct
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "不正解数",
                    value = "$totalIncorrect",
                    icon = Icons.Default.Close,
                    iconTint = Incorrect
                )
            }
        }

        // Accuracy card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "正答率",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${(accuracy * 100).toInt()}%",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (accuracy >= 0.7f) Correct else if (accuracy >= 0.5f) MaterialTheme.colorScheme.primary else Incorrect
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { accuracy },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (accuracy >= 0.7f) Correct else Incorrect,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "合計 $totalAttempts 回解答",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        // Weak words
        if (weakWords.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "苦手な単語",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            items(weakWords.take(10)) { word ->
                WeakWordItem(word)
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    iconTint: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun WeakWordItem(word: Word) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Incorrect.copy(alpha = 0.05f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = word.english,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = word.japanese,
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
            Text(
                text = "${(word.accuracy * 100).toInt()}%",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Incorrect
            )
        }
    }
}
