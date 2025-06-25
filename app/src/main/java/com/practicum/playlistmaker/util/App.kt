package com.practicum.playlistmaker.util

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.creator.Creator

const val PM_PREFERENCES = "pm_preferences"
const val DARK_THEME_KEY = "dark_theme_key"

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        Creator.initApplication(this)

        val darkThemeEnabled = Creator.getSharedPreferences(PM_PREFERENCES)
            .getBoolean(DARK_THEME_KEY, false)
        switchTheme(darkThemeEnabled)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}