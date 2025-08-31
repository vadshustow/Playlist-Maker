package com.practicum.playlistmaker.library.favorite.domain.repository

import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksRepository {

    suspend fun addTrackToFavorite(track: Track)

    suspend fun deleteTrackFromFavorite(track: Track)

    fun getAllFavoriteTracks(): Flow<List<Track>>

    suspend fun isTrackFavorite(trackId: Int): Boolean
}