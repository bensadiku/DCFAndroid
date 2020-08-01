package com.bensadiku.dcf.viewmodels

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bensadiku.dcf.models.NotificationTimer
import com.bensadiku.dcf.models.ThemeType
import com.bensadiku.dcf.util.Prefs
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SettingsViewModel @Inject constructor() : ViewModel() {

    private val _timeSelected = MutableLiveData<String>()
    val timeSelected: LiveData<String>
        get() = _timeSelected

    private var notificationHours: Int = 0
    private var notificationMinutes: Int = 0

    /**
     * ViewModel livedata state for the activity to check if the notifications are enabled
     */
    private val _onNotificationsStateChange = MutableLiveData<Boolean>()
    val onNotificationsStateChange: LiveData<Boolean>
        get() = _onNotificationsStateChange

    /**
     * Observe theme changes and re render UI
     */
    private val _themeType = MutableLiveData<ThemeType>(Prefs.getTheme())
    val themeType: LiveData<ThemeType>
        get() = _themeType

    var savedThemeType: ThemeType
        get() {
            return Prefs.getTheme()
        }
        set(theme) {
            setApplicationTheme(theme)
        }

    /**
     * To update the preferences and the lifecycle state which Compose is observing which will trigger a render
     */
    var hasNotificationsEnabled: Boolean
        get() {
            return Prefs.getHasNotificationsEnabled()
        }
        set(value) {
            _onNotificationsStateChange.value = value
            Prefs.setHasNotificationsEnabled(value)
        }

    /**
     * Called when the reset happens to the Settings to refresh the views with the default data
     */
    private fun initPreviousSettings() {
        val notificationTimer = Prefs.getNotificationTimeSeekbar()
        val timeUnit: TimeUnit = notificationTimer.timeUnit
        val interval = notificationTimer.interval
        val timeSelectedText: String = when (timeUnit) {
            TimeUnit.MINUTES -> {
                "$interval minutes."
            }
            TimeUnit.HOURS -> {
                "$interval hours."
            }
            else -> {
                " "
            }
        }
        _timeSelected.value = timeSelectedText

        hasNotificationsEnabled = Prefs.getHasNotificationsEnabled()
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
        return when (notificationTimer.timeUnit) {
            TimeUnit.HOURS -> {
                notificationTimer.interval * 4
            }
            TimeUnit.MINUTES -> {
                notificationTimer.interval % 4
            }
            else -> {
                notificationTimer.interval * 4
            }
        }
    }

    /**
     * Save the choice to the preferences and refreshes the view
     * If user selected more than 1 hr, ignore minutes and set TimeUnit.HOURS
     * else store minutes, ignore hrs and set TimeUnit.MINUTES
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

    private fun setApplicationTheme(themeType: ThemeType) {
        when (themeType) {
            ThemeType.DARK -> {
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES
                )
            }
            ThemeType.SYSTEM -> {
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                )
            }
            ThemeType.LIGHT -> {
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO
                )
            }
        }
        Prefs.setNewTheme(themeType)
        _themeType.value = themeType
    }

    fun reset() {
        Prefs.resetAll()
        initPreviousSettings()
    }
}