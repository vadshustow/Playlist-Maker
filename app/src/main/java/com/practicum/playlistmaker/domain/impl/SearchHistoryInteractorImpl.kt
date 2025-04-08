package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.model.Track
import com.practicum.playlistmaker.domain.repository.SearchHistoryRepository

class SearchHistoryInteractorImpl(private val repository: SearchHistoryRepository) : SearchHistoryInteractor {
    override fun addTrackToSearchHistory(track: Track) {
        repository.addTrackToSearchHistory(track)
    }

    override fun getHistoryList(): List<Track> {
        return repository.getHistoryList()
    }

    override fun clearSearchHistory() {
        repository.clearSearchHistory()
    }
}