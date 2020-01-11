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
        settingsViewModel =
            ViewModelProviders.of(this, viewModelFactory)[SettingsViewModel::class.java]

        binding.settingsNotificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            Timber.w(" allow notifications $isChecked")
            settingsViewModel.hasNotificationsEnabled = isChecked
        }

        binding.settingsResetBtn.setOnClickListener {
            settingsViewModel.reset()
            updateView()
            Timber.w("Reset everything happened")
        }

        if (BuildConfig.DEBUG) {
            binding.settingsNotificationView.setOnClickListener {
                PushNotification.show("testing", this)
            }
        }

        binding.settingsNotificationTimeSeekbar.setMax(24 * 4)
        /**
         * Calculate and save the new timer selected by user.
         * Minimum timer is 15 minutes(minimum timer allowed by work manager), max is 24 hrs.
         */
        binding.settingsNotificationTimeSeekbar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    settingsViewModel.calculateAndShowTimer(progress + 1)
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

        updateView()
    }

    /**
     * Used to update the view when we do a setting reset
     */
    private fun updateView() {
        settingsViewModel.initPreviousSettings()
        binding.settingsNotificationTimeSeekbar.setProgress(settingsViewModel.getNotificationProgressSeekbar())//update the progress to the default value
        binding.settingsNotificationSwitch.isChecked = settingsViewModel.hasNotificationsEnabled //update the switch to the default value
    }
}
