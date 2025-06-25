package com.practicum.playlistmaker.settings.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.settings.domain.api.ThemeInteractor
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val themeInteractor: ThemeInteractor,
) : ViewModel() {

    companion object {
        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(
                    Creator.provideSharingInteractor(),
                    Creator.provideThemeInteractor()
                )
            }
        }
    }

    private val _darkThemeEnabled = MutableLiveData<Boolean>()
    val darkThemeEnabled: LiveData<Boolean> get() = _darkThemeEnabled

    init {
        _darkThemeEnabled.value = themeInteractor.isDarkThemeEnabled()
    }

    fun setDarkThemeEnabled(enabled: Boolean) {
        themeInteractor.setDarkThemeEnabled(enabled)
        _darkThemeEnabled.value = enabled
    }

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun openTerms() {
        sharingInteractor.openTerms()
    }

    fun sendEmailToSupport() {
        sharingInteractor.openSupport()
    }
}