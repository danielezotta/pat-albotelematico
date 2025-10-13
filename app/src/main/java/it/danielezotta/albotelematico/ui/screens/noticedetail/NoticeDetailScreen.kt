package it.danielezotta.albotelematico.ui.screens.noticedetail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Domain
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import it.danielezotta.albotelematico.data.model.Notice
import it.danielezotta.albotelematico.ui.theme.ExpressiveTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoticeDetailScreen(
    notice: Notice,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val spacing = ExpressiveTheme.spacing

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dettaglio atto") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Indietro")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, notice.url)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Condividi atto"))
                    }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Condividi")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = spacing.extraLarge, vertical = spacing.large)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(spacing.large)
        ) {
            Text(
                text = notice.title,
                style = MaterialTheme.typography.headlineSmall
            )

            if (!notice.publishDate.isNullOrBlank() || !notice.expiryDate.isNullOrBlank()) {
                val dateLabel = buildString {
                    if (!notice.publishDate.isNullOrBlank()) {
                        append("Pubblicato il ")
                        append(notice.publishDate)
                    }
                    if (!notice.expiryDate.isNullOrBlank()) {
                        if (isNotEmpty()) append(" · ")
                        append("Visibile fino al ")
                        append(notice.expiryDate)
                    }
                }
                InfoRow(iconLabel = "Periodo", icon = Icons.Default.CalendarToday, value = dateLabel)
            }

            if (!notice.municipality.isNullOrBlank()) {
                InfoRow(iconLabel = "Comune", icon = Icons.Default.LocationOn, value = notice.municipality)
            }

            if (!notice.publisher.isNullOrBlank()) {
                InfoRow(iconLabel = "Pubblicato da", icon = Icons.Default.Domain, value = notice.publisher)
            }

            if (!notice.category.isNullOrBlank()) {
                AssistChip(
                    onClick = {},
                    label = { Text(notice.category) },
                    leadingIcon = { Icon(Icons.Default.Notifications, contentDescription = null) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        labelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        leadingIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                )
            }

            Text(
                text = "Descrizione",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = notice.description ?: "Nessuna descrizione disponibile",
                style = MaterialTheme.typography.bodyLarge
            )

            if (notice.attachments.isNotEmpty()) {
                Text(
                    text = "Allegati",
                    style = MaterialTheme.typography.titleMedium
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(spacing.small),
                    verticalArrangement = Arrangement.spacedBy(spacing.small)
                ) {
                    notice.attachments.forEach { attachment ->
                        AssistChip(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(attachment.url))
                                context.startActivity(intent)
                            },
                            label = { Text(attachment.name) },
                            leadingIcon = { Icon(Icons.Default.Attachment, contentDescription = null) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                labelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                leadingIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(spacing.medium))

            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(notice.url))
                    context.startActivity(intent)
                }
            ) {
                Text("Apri sul sito ufficiale")
            }
        }
    }
}

@Composable
private fun InfoRow(iconLabel: String, icon: androidx.compose.ui.graphics.vector.ImageVector, value: String) {
    val spacing = ExpressiveTheme.spacing
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        Icon(icon, contentDescription = iconLabel, tint = MaterialTheme.colorScheme.primary)
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = iconLabel,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic
            )
        }
    }
}
