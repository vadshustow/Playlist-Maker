package com.practicum.playlistmaker.library.playlist.data.repository

import com.practicum.playlistmaker.library.favorite.data.AppDatabase
import com.practicum.playlistmaker.library.playlist.data.converter.PlaylistDBConverter
import com.practicum.playlistmaker.library.playlist.data.converter.PlaylistTrackDBConverter
import com.practicum.playlistmaker.library.playlist.data.db.PlaylistEntity
import com.practicum.playlistmaker.library.playlist.domain.model.Playlist
import com.practicum.playlistmaker.library.playlist.domain.repository.PlaylistRepository
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDBConverter: PlaylistDBConverter,
    private val playlistTrackDBConverter: PlaylistTrackDBConverter,
): PlaylistRepository {

    override suspend fun createPlaylist(playlist: Playlist) {
        appDatabase.playlistDao().insertPlaylist(playlistDBConverter.map(playlist))
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = appDatabase.playlistDao().getAllPlaylists()
        emit(convertFromPlaylistEntity(playlists))
    }

    override suspend fun addTrackToPlaylist(
        playlist: Playlist,
        track: Track,
    ) {
        val existingTrack = appDatabase.playlistTrackDao().getTrackById(track.trackId)
        if (existingTrack == null) {
            appDatabase.playlistTrackDao().insertTrack(
                playlistTrackDBConverter.map(track)
            )
        }

        val updatedTrackIds = playlist.trackIds.toMutableList().apply {
            add(track.trackId)
        }
        val updatedPlaylist = playlist.copy(
            trackIds = updatedTrackIds,
            trackCount = updatedTrackIds.size
        )

        appDatabase.playlistDao().updatePlaylist(
            playlistDBConverter.map(updatedPlaylist)
        )
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist ->
            playlistDBConverter.map(playlist)
        }
    }
}