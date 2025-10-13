package it.danielezotta.albotelematico

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import it.danielezotta.albotelematico.ui.navigation.AppNavHost
import it.danielezotta.albotelematico.ui.theme.AlboTelematicoTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AlboTelematicoRoot()
        }
    }
}

@Composable
fun AlboTelematicoRoot() {
    AlboTelematicoTheme {
        // Set up system UI controller for status bar theming
        val systemUiController = rememberSystemUiController()
        systemUiController.setSystemBarsColor(
            color = MaterialTheme.colorScheme.primaryContainer,
            darkIcons = MaterialTheme.colorScheme.onPrimaryContainer.luminance() > 0.5f
        )
        
        // Set up navigation
        val navController = rememberNavController()
        
        // Main app surface
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AppNavHost(navController = navController)
        }
    }
}