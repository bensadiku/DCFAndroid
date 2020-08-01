package com.bensadiku.dcf.models

enum class ThemeType(val type: Int) {
    LIGHT(1),
    DARK(2),
    SYSTEM(-1);

    companion object {
        // Defaults to Dark theme if none
        fun getByValue(value: Int) = values().firstOrNull { it.type == value } ?: DARK
    }
}