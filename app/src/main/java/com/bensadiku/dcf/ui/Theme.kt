package com.bensadiku.dcf.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.bensadiku.dcf.models.ThemeType
import com.bensadiku.dcf.util.Prefs

private val LightThemeColors = lightColors(
    primary = Purple3,
    primaryVariant = Purple5,
    onPrimary = Color.White,
    secondary = Purple3,
    secondaryVariant = Purple5,
    onSecondary = Color.White,
    error = Purple4
)

private val DarkThemeColors = darkColors(
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