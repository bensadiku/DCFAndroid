package com.bensadiku.dcf.ui

import androidx.compose.Composable
import androidx.ui.foundation.isSystemInDarkTheme
import androidx.ui.graphics.Color
import androidx.ui.material.MaterialTheme
import androidx.ui.material.darkColorPalette
import androidx.ui.material.lightColorPalette
import com.bensadiku.dcf.models.ThemeType
import com.bensadiku.dcf.util.Prefs

private val LightThemeColors = lightColorPalette(
    primary = Purple3,
    primaryVariant = Purple5,
    onPrimary = Color.White,
    secondary = Purple3,
    secondaryVariant = Purple5,
    onSecondary = Color.White,
    error = Purple4
)

private val DarkThemeColors = darkColorPalette(
    primary = Purple2,
    primaryVariant = Purple3,
    onPrimary = Color.Black,
    secondary = Purple2,
    onSecondary = Color.White,
    error = Purple1
)

@Composable
fun CatFactTheme(
    darkTheme: Boolean = isSystemInDarkTheme() || Prefs.getTheme() == ThemeType.DARK,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (darkTheme) DarkThemeColors else LightThemeColors,
        content = content
    )
}