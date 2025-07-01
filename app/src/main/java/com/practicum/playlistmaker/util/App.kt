package com.practicum.playlistmaker.util

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.di.dataModule
import com.practicum.playlistmaker.di.interactorModule
import com.practicum.playlistmaker.di.repositoryModule
import com.practicum.playlistmaker.di.viewModelModule
import com.practicum.playlistmaker.settings.domain.api.ThemeInteractor
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

const val PM_PREFERENCES = "pm_preferences"
const val DARK_THEME_KEY = "dark_theme_key"

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                dataModule,
                interactorModule,
                repositoryModule,
                viewModelModule,
                )
        }

        val themeInteractor: ThemeInteractor = getKoin().get()
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