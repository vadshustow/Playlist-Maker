package com.practicum.playlistmaker.settings.data.repository

import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import com.practicum.playlistmaker.settings.domain.repository.ThemeRepository
import com.practicum.playlistmaker.utils.DARK_THEME_KEY

class ThemeRepositoryImpl(private val sharedPreferences: SharedPreferences) : ThemeRepository {


    override fun isDarkThemeEnabled(): Boolean {
        if (!sharedPreferences.contains(DARK_THEME_KEY)) {
            val isSystemDark = when (
                Resources.getSystem().configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            ) {
                Configuration.UI_MODE_NIGHT_YES -> true
                else -> false
            }
            setDarkThemeEnabled(isSystemDark)
            return isSystemDark
        }
        return sharedPreferences.getBoolean(DARK_THEME_KEY, false)
    }

    override fun setDarkThemeEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(DARK_THEME_KEY, enabled).apply()
    }
}