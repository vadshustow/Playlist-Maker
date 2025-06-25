package com.practicum.playlistmaker.settings.data.repository

import android.content.SharedPreferences
import com.practicum.playlistmaker.settings.domain.repository.ThemeRepository
import com.practicum.playlistmaker.util.DARK_THEME_KEY

class ThemeRepositoryImpl(private val sharedPreferences: SharedPreferences) : ThemeRepository {


    override fun isDarkThemeEnabled(): Boolean {
        return sharedPreferences.getBoolean(DARK_THEME_KEY, false)
    }

    override fun setDarkThemeEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(DARK_THEME_KEY, enabled).apply()
    }
}