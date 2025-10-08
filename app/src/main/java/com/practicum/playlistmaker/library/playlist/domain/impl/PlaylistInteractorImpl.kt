package com.practicum.playlistmaker.library.playlist.domain.impl

import com.practicum.playlistmaker.library.playlist.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.library.playlist.domain.model.Playlist
import com.practicum.playlistmaker.library.playlist.domain.repository.PlaylistRepository
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val repository: PlaylistRepository) : PlaylistInteractor {

    override suspend fun createPlaylist(playlist: Playlist) {
        repository.createPlaylist(playlist)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return repository.getAllPlaylists()
    }

    override suspend fun addTrackToPlaylist(
        playlist: Playlist,
        track: Track,
    ) {
        repository.addTrackToPlaylist(playlist, track)
    }

    override suspend fun getPlaylistById(playlistId: Int): Playlist? {
        return repository.getPlaylistById(playlistId)
    }

    override suspend fun getTracksByIds(trackIds: List<Int>): List<Track> {
        return repository.getTracksByIds(trackIds)
    }

    override suspend fun removeTrackFromPlaylist(
        playlist: Playlist,
        trackId: Int,
    ) {
        repository.removeTrackFromPlaylist(playlist, trackId)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        repository.deletePlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        repository.updatePlaylist(playlist)
    }
}