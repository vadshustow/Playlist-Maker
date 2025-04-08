package com.practicum.playlistmaker.ui

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.Creator

const val PM_PREFERENCES = "pm_preferences"
const val DARK_THEME_KEY = "dark_theme_key"

class App : Application() {

    private var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        Creator.initApplication(this)

        val sharedPrefs = Creator.getSharedPreferences(PM_PREFERENCES)
        darkTheme = sharedPrefs.getBoolean(DARK_THEME_KEY, false)
        switchTheme(darkTheme)

    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}