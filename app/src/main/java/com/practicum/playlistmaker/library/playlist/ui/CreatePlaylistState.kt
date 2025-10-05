package com.practicum.playlistmaker.library.playlist.ui

sealed class CreatePlaylistState {

    object Default : CreatePlaylistState()
    object Loading : CreatePlaylistState()
    object Success : CreatePlaylistState()
    data class Error(val message: String) : CreatePlaylistState()
}