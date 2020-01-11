package com.bensadiku.dcf.activities

import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bensadiku.dcf.BuildConfig
import androidx.lifecycle.ViewModelProvider
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

        if (BuildConfig.DEBUG) {
            binding.settingsNotificationView.setOnClickListener {
                PushNotification.show("testing", this)
            }
        }

        binding.settingsNotificationTimeSeekbar.setMax(24 * 4)
        binding.settingsNotificationTimeSeekbar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    settingsViewModel.setNotificationTimeSeekbar(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                settingsViewModel.saveNotificationTime()
            }
        })

        settingsViewModel.timeSelected.observe(this, Observer { time ->
            binding.settingsNotificationTimeHrsTextview.text = time
        })
    }
}
