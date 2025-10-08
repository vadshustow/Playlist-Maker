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

    override suspend fun getPlaylistById(playlistId: Int): Playlist? {
        val entity = appDatabase.playlistDao().getPlaylistById(playlistId)
        return entity?.let { playlistDBConverter.map(it) }
    }

    override suspend fun getTracksByIds(trackIds: List<Int>): List<Track> {
        val allTracks = appDatabase.playlistTrackDao().getAllTracks()
        val filtered = allTracks.filter { trackIds.contains(it.trackId) }
        return filtered.map { playlistTrackDBConverter.map(it) }
    }

    override suspend fun removeTrackFromPlaylist(
        playlist: Playlist,
        trackId: Int,
    ) {
        val updatedTrackIds = playlist.trackIds.toMutableList().apply {
            remove(trackId)
        }
        val updatedPlaylist = playlist.copy(
            trackIds = updatedTrackIds,
            trackCount = updatedTrackIds.size
        )
        appDatabase.playlistDao().updatePlaylist(
            playlistDBConverter.map(updatedPlaylist)
        )
        if (!isTrackUsedInAnyPlaylist(trackId)) {
            appDatabase.playlistTrackDao().deleteTrack(trackId)
        }
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        appDatabase.playlistDao().deletePlaylist(playlistDBConverter.map(playlist))
        playlist.trackIds.forEach { trackId ->
            if (!isTrackUsedInAnyOtherPlaylist(trackId, playlist.id)) {
                appDatabase.playlistTrackDao().deleteTrack(trackId)
            }
        }
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        appDatabase.playlistDao().updatePlaylist(playlistDBConverter.map(playlist))
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist ->
            playlistDBConverter.map(playlist)
        }
    }

    private suspend fun isTrackUsedInAnyPlaylist(trackId: Int): Boolean {
        val allPlaylists = appDatabase.playlistDao().getAllPlaylists()
        return allPlaylists.any { playlist ->
            val trackIds = playlistDBConverter.map(playlist).trackIds
            trackIds.contains(trackId)
        }
    }

    private suspend fun isTrackUsedInAnyOtherPlaylist(trackId: Int, excludePlaylistId: Int): Boolean {
        val allPlaylists = appDatabase.playlistDao().getAllPlaylists()
        return allPlaylists.any { playlistEntity ->
            if (playlistEntity.id == excludePlaylistId) return@any false

            val trackIds = playlistDBConverter.map(playlistEntity).trackIds
            trackIds.contains(trackId)
        }
    }
}