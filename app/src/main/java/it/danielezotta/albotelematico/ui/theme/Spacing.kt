package it.danielezotta.albotelematico.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class ExpressiveSpacing(
    val nano: Dp = 2.dp,
    val micro: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 12.dp,
    val large: Dp = 16.dp,
    val extraLarge: Dp = 24.dp,
    val huge: Dp = 32.dp
)

internal val LocalExpressiveSpacing = staticCompositionLocalOf { ExpressiveSpacing() }

object ExpressiveTheme {
    val spacing: ExpressiveSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalExpressiveSpacing.current
}
