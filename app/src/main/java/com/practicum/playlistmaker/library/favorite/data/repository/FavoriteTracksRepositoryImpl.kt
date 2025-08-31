package com.practicum.playlistmaker.library.favorite.data.repository

import com.practicum.playlistmaker.library.favorite.domain.repository.FavoriteTracksRepository
import com.practicum.playlistmaker.library.favorite.data.AppDatabase
import com.practicum.playlistmaker.library.favorite.data.converters.TrackDBConverter
import com.practicum.playlistmaker.library.favorite.data.db.TrackEntity
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.collections.map

class FavoriteTracksRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDBConverter: TrackDBConverter,
) : FavoriteTracksRepository {

    override suspend fun addTrackToFavorite(track: Track) {
        appDatabase.trackDao().insertTrack(trackDBConverter.map(track))
    }

    override suspend fun deleteTrackFromFavorite(track: Track) {
        appDatabase.trackDao().deleteTrack(trackDBConverter.map(track))
    }

    override fun getAllFavoriteTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.trackDao().getTracks()
        emit(convertFromTrackEntity(tracks))
    }

    override suspend fun isTrackFavorite(trackId: Int): Boolean {
        return appDatabase.trackDao().isTrackFavorite(trackId)
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDBConverter.map(track)}
    }


}