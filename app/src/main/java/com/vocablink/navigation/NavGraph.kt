package com.vocablink.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.vocablink.data.repository.WordRepository
import com.vocablink.ui.screens.QuizScreen
import com.vocablink.ui.screens.StatsScreen
import com.vocablink.ui.screens.WordListScreen
import com.vocablink.viewmodel.QuizViewModel
import com.vocablink.viewmodel.StatsViewModel
import com.vocablink.viewmodel.WordListViewModel

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object WordList : Screen("word_list", "単語帳", Icons.Default.Home)
    data object Quiz : Screen("quiz", "クイズ", Icons.Default.PlayArrow)
    data object Stats : Screen("stats", "統計", Icons.Default.Star)
}

val bottomNavItems = listOf(Screen.WordList, Screen.Quiz, Screen.Stats)

@Composable
fun VocabLinkNavGraph(repository: WordRepository) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.WordList.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.WordList.route) {
                val vm: WordListViewModel = viewModel(
                    factory = WordListViewModel.Factory(repository)
                )
                WordListScreen(viewModel = vm)
            }
            composable(Screen.Quiz.route) {
                val vm: QuizViewModel = viewModel(
                    factory = QuizViewModel.Factory(repository)
                )
                QuizScreen(viewModel = vm)
            }
            composable(Screen.Stats.route) {
                val vm: StatsViewModel = viewModel(
                    factory = StatsViewModel.Factory(repository)
                )
                StatsScreen(viewModel = vm)
            }
        }
    }
}
