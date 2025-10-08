package com.practicum.playlistmaker.library.playlist.ui

import com.practicum.playlistmaker.library.playlist.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track

data class PlaylistInfoState(
    val playlist: Playlist? = null,
    val tracks: List<Track> = emptyList()
)
