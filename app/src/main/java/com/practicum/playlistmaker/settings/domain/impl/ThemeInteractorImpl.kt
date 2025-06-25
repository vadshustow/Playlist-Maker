package com.practicum.playlistmaker.settings.domain.impl

import com.practicum.playlistmaker.settings.domain.api.ThemeInteractor
import com.practicum.playlistmaker.settings.domain.repository.ThemeRepository

class ThemeInteractorImpl(private val repository: ThemeRepository) : ThemeInteractor {

    override fun isDarkThemeEnabled(): Boolean {
        return repository.isDarkThemeEnabled()
    }

    override fun setDarkThemeEnabled(enabled: Boolean) {
        return repository.setDarkThemeEnabled(enabled)
    }
}