package com.hannoobz.friendlock.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF89B4FA),
    onPrimary = Color(0xFF1E1E2E),
    primaryContainer = Color(0xFF585B70),
    onPrimaryContainer = Color(0xFFCDD6F4),

    secondary = Color(0xFFF38BA8),
    onSecondary = Color(0xFF1E1E2E),
    secondaryContainer = Color(0xFF45475A),
    onSecondaryContainer = Color(0xFFF2CDCD),

    tertiary = Color(0xFFA6E3A1),
    onTertiary = Color(0xFF1E1E2E),
    tertiaryContainer = Color(0xFF313244),
    onTertiaryContainer = Color(0xFFC9E9C0),

    background = Color(0xFF1E1E2E),
    onBackground = Color(0xFFD9E0EE),

    surface = Color(0xFF1E1E2E),
    onSurface = Color(0xFFD9E0EE),

    surfaceVariant = Color(0xFF45475A),
    onSurfaceVariant = Color(0xFFBAC2DE),

    error = Color(0xFFF38BA8),
    onError = Color(0xFF1E1E2E),
    errorContainer = Color(0xFF524149),
    onErrorContainer = Color(0xFFFFE5E5),

    outline = Color(0xFF6C7086),
    inverseOnSurface = Color(0xFF1E1E2E),
    inverseSurface = Color(0xFFD9E0EE),
    inversePrimary = Color(0xFF89B4FA),
    surfaceTint = Color(0xFF89B4FA),
    outlineVariant = Color(0xFF6C7086),
    scrim = Color(0xFF000000)
)




private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun FriendLockTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )

}