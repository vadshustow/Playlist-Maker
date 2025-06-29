package com.practicum.playlistmaker.search.ui

import com.practicum.playlistmaker.search.domain.model.Track

sealed class SearchState {

    object Loading: SearchState()
    data class Tracks(val tracks: List<Track>) : SearchState()
    data class History(val history: List<Track>) : SearchState()
    data class Error(val message: String, val imageRes: Int) : SearchState()

}