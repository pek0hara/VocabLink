package com.vocablink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.vocablink.navigation.VocabLinkNavGraph
import com.vocablink.ui.theme.VocabLinkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val application = application as VocabLinkApplication

        setContent {
            VocabLinkTheme {
                VocabLinkNavGraph(repository = application.repository)
            }
        }
    }
}
