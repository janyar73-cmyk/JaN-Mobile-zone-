package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
  primary = BrandVibrantBlue,
  onPrimary = Color.White,
  primaryContainer = EasyloadBlueBg,
  onPrimaryContainer = BrandDarkBlue,
  secondary = EasypaisaPurple,
  onSecondary = Color.White,
  secondaryContainer = EasypaisaPurpleBg,
  onSecondaryContainer = EasypaisaPurple,
  tertiary = CashGreen,
  onTertiary = Color.White,
  tertiaryContainer = CashGreenBg,
  onTertiaryContainer = CashGreen,
  background = SurfaceBackground,
  onBackground = TextPrimary,
  surface = CardSurface,
  onSurface = TextPrimary,
  surfaceVariant = InputBackground,
  onSurfaceVariant = TextSecondary,
  outline = BorderColor,
  error = ExpenseRed,
  onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
  primary = BrandVibrantBlue,
  onPrimary = Color.White,
  secondary = EasypaisaPurple,
  onSecondary = Color.White,
  tertiary = CashGreen,
  background = BrandDarkBlue,
  onBackground = Color.White,
  surface = BrandNavy,
  onSurface = Color.White,
  surfaceVariant = Color(0xFF1E293B),
  onSurfaceVariant = Color(0xFFCBD5E1),
  outline = Color(0xFF334155),
  error = ExpenseRed,
  onError = Color.White
)

@Composable
fun JANMobileZoneTheme(
  darkTheme: Boolean = false, // Default to clean professional light theme matching reference
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as? Activity)?.window
      if (window != null) {
        window.statusBarColor = BrandDarkBlue.toArgb()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
      }
    }
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
