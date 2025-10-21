package it.danielezotta.albotelematico.ui.screens.tenders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import it.danielezotta.albotelematico.ui.theme.ExpressiveTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TendersScreen(
    onBackClick: () -> Unit
) {
    val spacing = ExpressiveTheme.spacing
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bandi di gara") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = spacing.extraLarge, vertical = spacing.large),
            tonalElevation = 2.dp,
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(spacing.large),
                verticalArrangement = Arrangement.spacedBy(spacing.medium),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Elenco bandi di gara in arrivo",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Presto potrai consultare i bandi in questa sezione.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpiredTendersScreen(
    onBackClick: () -> Unit
) {
    val spacing = ExpressiveTheme.spacing
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bandi scaduti") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = spacing.extraLarge, vertical = spacing.large),
            tonalElevation = 2.dp,
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(spacing.large),
                verticalArrangement = Arrangement.spacedBy(spacing.medium),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Archivio bandi scaduti in arrivo",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Stiamo lavorando per rendere disponibile l'archivio degli atti.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
