package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.model.Track

interface SearchHistoryInteractor {
    fun addTrackToSearchHistory(track: Track)
    fun getHistoryList(): List<Track>
    fun clearSearchHistory()
}