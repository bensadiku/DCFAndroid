package com.bensadiku.dcf.util

import android.content.Context
import android.content.SharedPreferences
import com.bensadiku.dcf.CatApplication

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

    fun resetAll() {
        getSharedPreferences().edit().clear().apply()
    }
}