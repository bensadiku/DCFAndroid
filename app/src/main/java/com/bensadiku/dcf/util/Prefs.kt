package com.bensadiku.dcf.util

import android.content.Context
import android.content.SharedPreferences
import com.bensadiku.dcf.CatApplication
import com.bensadiku.dcf.models.NotificationTimer
import com.google.gson.Gson
import timber.log.Timber

object Prefs {

    private fun getSharedPreferences(): SharedPreferences {
        return CatApplication.instance!!
            .getSharedPreferences("CatFactApp", Context.MODE_PRIVATE)
    }

    fun setHasNotificationsEnabled(enabled: Boolean) {
        getSharedPreferences().edit().putBoolean("Notifications_Enabled", enabled).apply()
    }

    fun getHasNotificationsEnabled(): Boolean {
        return getSharedPreferences().getBoolean(
            "Notifications_Enabled",
            true    //by default send notifications
        )
    }

    /**
     * @param notificationTimer is the interval in which we periodically send the user notifications
     * Serialize to json and save to preferences
     */
    fun setNotificationTimeSeekbar(notificationTimer: NotificationTimer) {
        val timer = Gson().toJson(notificationTimer)
        getSharedPreferences().edit().putString("Notifications_Timer", timer).apply()
        Timber.w("Changed notification timer to $notificationTimer")
    }

    /**
     * @return the interval in which we periodically send the user notifications
     * If there's no info stored to the preferences, return the default timers
     */
    fun getNotificationTimeSeekbar(): NotificationTimer {
        val json: String? = getSharedPreferences().getString("Notifications_Timer", null)
        return if (json == null || json.isEmpty()) {
            val timer = NotificationTimer()
            Timber.w("Retrieved notification timer $timer")
            timer
        } else {
            val timer = Gson().fromJson(json, NotificationTimer::class.java)
            Timber.w("Retrieved notification timer $timer")
            timer
        }
    }

    fun resetAll() {
        getSharedPreferences().edit().clear().apply()
    }
}