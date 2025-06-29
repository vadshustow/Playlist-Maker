package com.practicum.playlistmaker.search.domain.repository

import com.practicum.playlistmaker.search.domain.model.Track

interface SearchHistoryRepository {
    fun addTrackToSearchHistory(track: Track)
    fun getHistoryList(): List<Track>
    fun clearSearchHistory()
}