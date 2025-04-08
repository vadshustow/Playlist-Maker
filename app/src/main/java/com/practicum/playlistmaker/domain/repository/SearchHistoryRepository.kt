package com.practicum.playlistmaker.domain.repository

import com.practicum.playlistmaker.domain.model.Track

interface SearchHistoryRepository {
    fun addTrackToSearchHistory(track: Track)
    fun getHistoryList(): List<Track>
    fun clearSearchHistory()
}