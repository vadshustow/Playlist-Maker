package com.practicum.playlistmaker.library.playlist.ui

import com.practicum.playlistmaker.library.playlist.domain.model.Playlist

sealed class PlaylistState {

    object Empty: PlaylistState()

    data class Content(val playlists: List<Playlist>): PlaylistState()
}