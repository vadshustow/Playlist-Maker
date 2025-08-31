package com.practicum.playlistmaker.library.favorite.domain.impl

import com.practicum.playlistmaker.library.favorite.domain.repository.FavoriteTracksRepository
import com.practicum.playlistmaker.library.favorite.domain.api.FavoriteTracksInteractor
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

class FavoriteTracksInteractorImpl(
    private val repository: FavoriteTracksRepository
) : FavoriteTracksInteractor {

    override suspend fun addTrackToFavorite(track: Track) {
        repository.addTrackToFavorite(track)
    }

    override suspend fun deleteTrackFromFavorite(track: Track) {
        repository.deleteTrackFromFavorite(track)
    }

    override fun getAllFavoriteTracks(): Flow<List<Track>> {
        return repository.getAllFavoriteTracks()
    }

    override suspend fun isTrackFavorite(trackId: Int): Boolean {
        return repository.isTrackFavorite(trackId)
    }
}