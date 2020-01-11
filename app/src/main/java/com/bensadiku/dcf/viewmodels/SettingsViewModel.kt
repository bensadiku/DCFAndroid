package com.bensadiku.dcf.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bensadiku.dcf.models.NotificationTimer
import com.bensadiku.dcf.util.Prefs
import timber.log.Timber
import javax.inject.Inject

class SettingsViewModel @Inject constructor(): ViewModel() {

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

    /**
     * Called when the Activity first starts to update the views
     * Also called when the reset happens to the Settings to refresh the views with the default data
     */
     fun initPreviousSettings() {
        val notificationTimer = Prefs.getNotificationTimeSeekbar()
        notificationHours = notificationTimer.hr
        notificationMinutes = notificationTimer.minutes
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

    /**
     * Called as the user is dragging the seekbar to calculate and update the view
     */
    fun calculateAndShowTimer(progress: Int) {
        notificationHours = progress / 4 // it will return hours.
        notificationMinutes = progress % 4 * 15 // here will be minutes.
        _timeSelected.value = "$notificationHours hours $notificationMinutes minutes"
    }

    fun getNotificationProgressSeekbar(): Int {
        val notificationTimer = Prefs.getNotificationTimeSeekbar()
        return notificationTimer.hr * 4
    }

    /**
     * Second part saves the choice to the preferences and refreshes the view
     */
    fun saveNotificationTime() {
        Timber.w("Saving the timer")
        Prefs.setNotificationTimeSeekbar(NotificationTimer(hr = notificationHours, minutes = notificationMinutes))
    }

    fun reset(){
        Prefs.resetAll()
    }
}