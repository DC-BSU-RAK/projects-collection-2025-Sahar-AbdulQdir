package com.example.multiviewsapp

import android.content.Context
import android.content.SharedPreferences

object AppSettingsManager {
    private const val PREFS_NAME = "AppPrefs"
    private const val KEY_MUTE_TOAST = "MUTE_TOAST"
    private const val KEY_MUTE_SOUND = "MUTE_SOUND"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun isToastMuted(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_MUTE_TOAST, false)
    }

    fun setToastMuted(context: Context, value: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_MUTE_TOAST, value).apply()
    }

    fun isSoundMuted(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_MUTE_SOUND, false)
    }

    fun setSoundMuted(context: Context, value: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_MUTE_SOUND, value).apply()
    }
}
