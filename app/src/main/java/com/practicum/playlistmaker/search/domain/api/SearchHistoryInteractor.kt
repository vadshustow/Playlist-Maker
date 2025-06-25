package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.model.Track

interface SearchHistoryInteractor {
    fun addTrackToSearchHistory(track: Track)
    fun getHistoryList(): List<Track>
    fun clearSearchHistory()
}