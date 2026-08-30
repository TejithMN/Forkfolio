package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ui.ForkfolioViewModel
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.ForkfolioTheme

class MainActivity : ComponentActivity() {
    private val viewModel: ForkfolioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ForkfolioTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = CreamBackground
                ) {
                    HomeScreen(viewModel = viewModel)
                }
            }
        }
    }
}

