package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.library.favorite.ui.view_model.FavoriteTracksViewModel
import com.practicum.playlistmaker.library.ui.view_model.PlaylistsViewModel
import com.practicum.playlistmaker.player.ui.view_model.AudioPlayerViewModel
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel { (url: String, track: Track) ->
        AudioPlayerViewModel(get(),get(), get(), url, track)
    }

    viewModel {
        SearchViewModel(
            androidApplication(),
            get(),
            get()
        )
    }

    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel {
        FavoriteTracksViewModel(get())
    }

    viewModel {
        PlaylistsViewModel()
    }
}