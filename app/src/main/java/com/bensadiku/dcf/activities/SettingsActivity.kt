package com.bensadiku.dcf.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.bensadiku.dcf.CatApplication
import com.bensadiku.dcf.databinding.ActivitySettingsBinding
import com.bensadiku.dcf.util.PushNotification
import com.bensadiku.dcf.viewmodels.SettingsViewModel
import timber.log.Timber
import javax.inject.Inject

class SettingsActivity : AppCompatActivity() {

    lateinit var binding: ActivitySettingsBinding
    lateinit var settingsViewModel: SettingsViewModel
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //inject this activity
        (application as CatApplication).getComponent().inject(this)

        //viewmodel
        settingsViewModel = ViewModelProviders.of(this, viewModelFactory)[SettingsViewModel::class.java]

        binding.settingsNotificationSwitch.isChecked = settingsViewModel.hasNotificationsEnabled
        binding.settingsNotificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            Timber.w(" allow notifications $isChecked")
            settingsViewModel.hasNotificationsEnabled = isChecked
        }

        binding.settingsNotificationView.setOnClickListener {
            PushNotification.show("testing",this)
        }
    }
}
