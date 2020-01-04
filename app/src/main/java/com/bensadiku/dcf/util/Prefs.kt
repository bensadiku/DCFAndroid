package com.bensadiku.dcf.util

import android.content.Context
import android.content.SharedPreferences
import com.bensadiku.dcf.CatApplication

object Prefs {

    private fun getSharedPreferences(): SharedPreferences {
        return CatApplication.instance!!
            .getSharedPreferences("CatFactApp", Context.MODE_PRIVATE)
    }

    fun resetAll() {
        getSharedPreferences().edit().clear().apply()
    }
}