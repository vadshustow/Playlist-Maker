package com.practicum.playlistmaker.library.playlist.domain.api

import com.practicum.playlistmaker.library.playlist.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {

    suspend fun createPlaylist(playlist: Playlist)

    fun getAllPlaylists(): Flow<List<Playlist>>

    suspend fun addTrackToPlaylist(playlist: Playlist, track: Track)

    suspend fun getPlaylistById(playlistId: Int): Playlist?

    suspend fun getTracksByIds(trackIds: List<Int>): List<Track>

    suspend fun removeTrackFromPlaylist(playlist: Playlist, trackId: Int)

    suspend fun deletePlaylist(playlist: Playlist)

    suspend fun updatePlaylist(playlist: Playlist)
}