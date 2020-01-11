package com.bensadiku.dcf.viewmodels

import androidx.lifecycle.ViewModel
import com.bensadiku.dcf.util.Prefs
import javax.inject.Inject

class SettingsViewModel @Inject constructor(): ViewModel() {


    var hasNotificationsEnabled: Boolean
        get() {
            return Prefs.getHasNotificationsEnabled()
        }
        set(value) {
            Prefs.setHasNotificationsEnabled(value)
        }
}