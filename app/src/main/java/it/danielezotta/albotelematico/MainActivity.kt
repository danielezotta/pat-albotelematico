package it.danielezotta.albotelematico

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import it.danielezotta.albotelematico.ui.navigation.AppNavHost
import it.danielezotta.albotelematico.ui.theme.AlboTelematicoTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AlboTelematicoRoot()
        }
    }
}

@Composable
fun AlboTelematicoRoot() {
    AlboTelematicoTheme {
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