package com.practicum.playlistmaker.creator

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.player.data.repository.PlayerRepositoryImpl
import com.practicum.playlistmaker.search.data.repository.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.repository.TRACK_LIST_KEY
import com.practicum.playlistmaker.search.data.repository.TracksRepositoryImpl
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.search.domain.impl.SearchHistoryInteractorImpl
import com.practicum.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.practicum.playlistmaker.player.domain.repository.PlayerRepository
import com.practicum.playlistmaker.search.domain.repository.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.repository.TracksRepository
import com.practicum.playlistmaker.settings.data.repository.ThemeRepositoryImpl
import com.practicum.playlistmaker.settings.domain.api.ThemeInteractor
import com.practicum.playlistmaker.settings.domain.impl.ThemeInteractorImpl
import com.practicum.playlistmaker.settings.domain.repository.ThemeRepository
import com.practicum.playlistmaker.sharing.data.repository.ExternalNavigatorRepositoryImpl
import com.practicum.playlistmaker.sharing.data.repository.SharingRepositoryImpl
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import com.practicum.playlistmaker.sharing.domain.repository.ExternalNavigatorRepository
import com.practicum.playlistmaker.sharing.domain.repository.SharingRepository
import com.practicum.playlistmaker.util.PM_PREFERENCES

object Creator {
    private lateinit var application: Application

    fun initApplication(application: Application) {
        this.application = application
    }

    fun getSharedPreferences(key: String): SharedPreferences {
        return application.getSharedPreferences(key, Context.MODE_PRIVATE)
    }

    fun getTrackRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTrackRepository())
    }

    fun getSearchHistoryRepository(): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(getSharedPreferences(TRACK_LIST_KEY))
    }

    fun provideSearchHistoryInteractor(): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(getSearchHistoryRepository())
    }

    fun getPlayerRepository(): PlayerRepository {
        return PlayerRepositoryImpl()
    }

    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(getPlayerRepository())
    }

    fun getSharingRepository(): SharingRepository {
        return SharingRepositoryImpl(application)
    }

    fun getExternalNavigatorRepository(): ExternalNavigatorRepository {
        return ExternalNavigatorRepositoryImpl(application)
    }

    fun provideSharingInteractor(): SharingInteractor {
        return SharingInteractorImpl(getExternalNavigatorRepository(), getSharingRepository())
    }

    fun getThemeRepository(): ThemeRepository {
        return ThemeRepositoryImpl(getSharedPreferences(PM_PREFERENCES))
    }

    fun provideThemeInteractor(): ThemeInteractor {
        return ThemeInteractorImpl(getThemeRepository())
    }
}