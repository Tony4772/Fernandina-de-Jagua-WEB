package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
  primary = ChocolatePrimaryContainer,
  onPrimary = OnChocolatePrimary,
  primaryContainer = ChocolatePrimaryContainer,
  onPrimaryContainer = SecondaryFixed,
  secondary = GoldSecondary,
  onSecondary = Color.White,
  secondaryContainer = GoldSecondaryContainer,
  onSecondaryContainer = OnGoldSecondaryContainer,
  tertiary = TertiaryBrown,
  onTertiary = Color.White,
  background = CreamBackground,
  onBackground = OnSurfaceDark,
  surface = CreamSurface,
  onSurface = OnSurfaceDark,
  surfaceVariant = CreamSurfaceContainer,
  onSurfaceVariant = OnSurfaceVariant,
  outline = OutlineGrey,
  outlineVariant = OutlineVariant,
  error = ErrorRed,
  onError = OnError,
  errorContainer = ErrorContainer
)

private val DarkColorScheme = darkColorScheme(
  primary = SecondaryFixedDim,
  onPrimary = ChocolatePrimaryContainer,
  primaryContainer = ChocolatePrimaryContainer,
  onPrimaryContainer = SecondaryFixed,
  secondary = GoldSecondaryContainer,
  onSecondary = OnSecondaryFixed,
  secondaryContainer = GoldSecondary,
  onSecondaryContainer = SecondaryFixed,
  tertiary = TertiaryBrown,
  onTertiary = Color.White,
  background = Color(0xFF1E1A18),
  onBackground = CreamBackground,
  surface = Color(0xFF241F1D),
  onSurface = CreamBackground,
  surfaceVariant = Color(0xFF2D2623),
  onSurfaceVariant = CreamSurfaceContainerHighest,
  outline = OutlineGrey,
  error = ErrorRed,
  onError = OnError
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Use our brand colors
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
