package com.bensadiku.dcf.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bensadiku.dcf.util.Prefs
import timber.log.Timber

class SettingsViewModel: ViewModel() {

    private val _timeSelected = MutableLiveData<String>()
    val timeSelected: LiveData<String>
        get() = _timeSelected

    private var notificationHours: Int = 0
    private var notificationMinutes: Int = 0

    var hasNotificationsEnabled: Boolean
        get() {
            return Prefs.getHasNotificationsEnabled()
        }
        set(value) {
            Prefs.setHasNotificationsEnabled(value)
        }

    fun setNotificationTimeSeekbar(progress: Int) {
        notificationHours = progress / 4 // it will return hours.
        notificationMinutes = progress % 4 * 15 // here will be minutes.

        _timeSelected.value = "$notificationHours hours $notificationMinutes minutes"
    }

    /**
     * First part of it is just UX, if there's 0 minutes/hours on the counter, hide it.
     * Second part saves the choice to the preferences.
     * TODO, Save the info to the preferences, read it from work manager
     */
    fun saveNotificationTime() {
        val timeSelectedText: String = when {
            notificationHours == 0 -> {
                "$notificationMinutes minutes."
            }
            notificationMinutes == 0 -> {
                "$notificationHours hours."
            }
            else -> {
                "$notificationHours hours $notificationMinutes minutes."
            }
        }
        _timeSelected.value = timeSelectedText
    }
}