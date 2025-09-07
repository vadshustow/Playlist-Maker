package com.practicum.playlistmaker.library.favorite.ui

import com.practicum.playlistmaker.search.domain.model.Track

sealed class FavoriteTracksState {

    object Empty: FavoriteTracksState()

    data class Content(val tracks: List<Track>) : FavoriteTracksState()
}