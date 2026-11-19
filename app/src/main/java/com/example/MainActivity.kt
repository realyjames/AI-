package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.data.PreferencesRepository
import com.example.data.TranslationService
import com.example.ui.GreetingDesignerScreen
import com.example.ui.PromptViewModel
import com.example.ui.theme.GreetingPromptTheme

class MainActivity : ComponentActivity() {

    private val viewModel: PromptViewModel by viewModels {
        PromptViewModel.Factory(
            repository = PreferencesRepository(applicationContext),
            translationService = TranslationService()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GreetingPromptTheme {
                GreetingDesignerScreen(viewModel = viewModel)
            }
        }
    }
}
