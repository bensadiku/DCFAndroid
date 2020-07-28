package com.bensadiku.dcf

import androidx.compose.remember
import androidx.ui.test.ComposeTestRule
import com.bensadiku.dcf.repository.CatFactApiRepository
import com.bensadiku.dcf.ui.MainApp
import com.bensadiku.dcf.ui.SettingsApp
import com.bensadiku.dcf.viewmodels.MainViewModel
import com.bensadiku.dcf.viewmodels.SettingsViewModel
import dagger.Provides

/**
 * Launches the app from a test context with the settings activity view
 */
fun ComposeTestRule.launchSettingsApp() {
    setContent {
        SettingsApp(
            remember { SettingsViewModel() }
        )
    }
}

/**
 * Launches the app from a test context with the main activity view
 */
fun ComposeTestRule.launchMainApp(mainViewModel: MainViewModel) {
    setContent {
        MainApp(
            remember { mainViewModel }
        )
    }
}


