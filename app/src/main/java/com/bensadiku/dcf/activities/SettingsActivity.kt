package com.bensadiku.dcf.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.bensadiku.dcf.CatApplication
import com.bensadiku.dcf.ui.SettingsApp
import com.bensadiku.dcf.viewmodels.SettingsViewModel
import javax.inject.Inject

class SettingsActivity : AppCompatActivity() {

    private lateinit var settingsViewModel: SettingsViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //inject this activity
        (application as CatApplication).getComponent().inject(this)

        //viewmodel
        settingsViewModel =
            ViewModelProviders.of(this, viewModelFactory)[SettingsViewModel::class.java]

        setContent {
            SettingsApp(settingsViewModel = settingsViewModel)
        }
    }
}
