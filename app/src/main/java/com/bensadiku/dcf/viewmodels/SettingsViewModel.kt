package com.bensadiku.dcf.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bensadiku.dcf.models.NotificationTimer
import com.bensadiku.dcf.util.Prefs
import timber.log.Timber
import java.util.concurrent.TimeUnit
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
        val timeUnit: TimeUnit = notificationTimer.timeUnit
        val interval = notificationTimer.interval
        val timeSelectedText: String = when {
            timeUnit == TimeUnit.MINUTES -> {
                "$interval minutes."
            }
            timeUnit == TimeUnit.HOURS -> {
                "$interval hours."
            }
            else -> {
                " "
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

        val timeSelectedText: String = when {
            notificationHours > 0 -> {
                "$notificationHours hours."
            }
            notificationHours == 0 -> {
                "$notificationMinutes minutes."
            }
            else -> {
                "$notificationHours hours $notificationMinutes minutes."
            }
        }
        _timeSelected.value = timeSelectedText
    }

    fun getNotificationProgressSeekbar(): Int {
        val notificationTimer = Prefs.getNotificationTimeSeekbar()
        return notificationTimer.interval * 4
    }

    /**
     * Save the choice to the preferences and refreshes the view
     * If user selected more than 1 hr, ignore minutes and set TimeUnit.MINUTES
     * else store minutes and ignore hrs and set TimeUnit.HOURS
     */
    fun saveNotificationTime() {
        Timber.w("Saving the timer")
        val notificationTimer: NotificationTimer = if (notificationHours == 0) {
            NotificationTimer(interval = notificationMinutes, timeUnit = TimeUnit.MINUTES)
        } else {
            NotificationTimer(interval = notificationHours, timeUnit = TimeUnit.HOURS)
        }
        Prefs.setNotificationTimeSeekbar(notificationTimer)
    }

    fun reset(){
        Prefs.resetAll()
    }
}