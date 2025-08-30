package com.practicum.playlistmaker.search.domain.repository

import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    fun searchTracks(term: String): Flow<Result<List<Track>>>
}