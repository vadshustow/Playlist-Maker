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

        val themeInteractor = Creator.provideThemeInteractor()
        val darkThemeEnabled = themeInteractor.isDarkThemeEnabled()
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