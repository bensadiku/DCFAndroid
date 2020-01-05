package com.bensadiku.dcf.viewmodels

import androidx.lifecycle.ViewModel
import com.bensadiku.dcf.util.Prefs

class SettingsViewModel: ViewModel() {


    var hasNotificationsEnabled: Boolean
        get() {
            return Prefs.getHasNotificationsEnabled()
        }
        set(value) {
            Prefs.setHasNotificationsEnabled(value)
        }
}