package com.practicum.playlistmaker.library.playlist.ui

import com.practicum.playlistmaker.library.playlist.domain.model.Playlist

sealed class CreatePlaylistState {

    object Default : CreatePlaylistState()
    object Loading : CreatePlaylistState()
    object Success : CreatePlaylistState()
    data class Error(val message: String) : CreatePlaylistState()
    data class PlaylistLoaded(val playlist: Playlist) : CreatePlaylistState()
}