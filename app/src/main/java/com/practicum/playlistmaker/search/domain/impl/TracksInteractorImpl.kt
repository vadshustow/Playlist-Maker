package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.domain.repository.TracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

    override fun searchTracks(term: String): Flow<Pair<List<Track>?, String?>> {
        return repository.searchTracks(term).map { result ->
            result.fold(
                onSuccess = { data -> Pair(data, null) },
                onFailure = { error -> Pair(null, error.message) }
            )
        }
    }
}