package com.bensadiku.dcf.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.bensadiku.dcf.databinding.ActivitySettingsBinding
import com.bensadiku.dcf.util.PushNotification
import com.bensadiku.dcf.viewmodels.SettingsViewModel
import timber.log.Timber

class SettingsActivity : AppCompatActivity() {

    lateinit var binding: ActivitySettingsBinding
    lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settingsViewModel = ViewModelProviders.of(this)[SettingsViewModel::class.java]

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
