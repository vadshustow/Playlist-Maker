package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.player.data.repository.PlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.repository.PlayerRepository
import com.practicum.playlistmaker.search.data.repository.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.repository.TracksRepositoryImpl
import com.practicum.playlistmaker.search.domain.repository.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.repository.TracksRepository
import com.practicum.playlistmaker.settings.data.repository.ThemeRepositoryImpl
import com.practicum.playlistmaker.settings.domain.repository.ThemeRepository
import com.practicum.playlistmaker.sharing.data.repository.ExternalNavigatorRepositoryImpl
import com.practicum.playlistmaker.sharing.data.repository.SharingRepositoryImpl
import com.practicum.playlistmaker.sharing.domain.repository.ExternalNavigatorRepository
import com.practicum.playlistmaker.sharing.domain.repository.SharingRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {

    factory<PlayerRepository> {
        PlayerRepositoryImpl(get())
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get(), get())
    }

    single<TracksRepository> {
        TracksRepositoryImpl(get())
    }

    single<ThemeRepository> {
        ThemeRepositoryImpl(get())
    }

    single<ExternalNavigatorRepository> {
        ExternalNavigatorRepositoryImpl(androidContext())
    }

    single<SharingRepository> {
        SharingRepositoryImpl(androidContext())
    }
}