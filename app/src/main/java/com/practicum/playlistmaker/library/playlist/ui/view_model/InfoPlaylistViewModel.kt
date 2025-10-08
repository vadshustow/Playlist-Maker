package com.practicum.playlistmaker.library.playlist.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.playlist.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.library.playlist.ui.PlaylistInfoState
import com.practicum.playlistmaker.utils.Transform
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InfoPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
): ViewModel() {

    private val _state = MutableStateFlow(PlaylistInfoState())
    val state: StateFlow<PlaylistInfoState> = _state

    fun loadPlaylistInfo(playlistId: Int) {
        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylistById(playlistId)
            val tracks = playlist?.let { playlistInteractor.getTracksByIds(it.trackIds) } ?: emptyList()
            _state.value = PlaylistInfoState(playlist, tracks)
        }
    }

    fun removeTrackFromPlaylist(trackId: Int) {
        viewModelScope.launch {
            val playlist = state.value.playlist ?: return@launch
            playlistInteractor.removeTrackFromPlaylist(playlist, trackId)
            loadPlaylistInfo(playlist.id)
        }
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            val playlist = state.value.playlist ?: return@launch
            playlistInteractor.deletePlaylist(playlist)
        }
    }

    fun getShareText(): String? {
        val playlist = state.value.playlist ?: return null
        val tracks = state.value.tracks
        if (tracks.isEmpty()) {
            return null
        }
        val trackList = tracks.mapIndexed { index, track ->
            "${index + 1}. ${track.artistName} - ${track.trackName} (${track.trackTimeMillis})"
        }.joinToString("\n")
        return """
            ${playlist.name}
            ${playlist.description ?: ""}
            ${playlist.trackCount} ${Transform.getTracksCountText(playlist.trackCount)}
            $trackList
        """.trimIndent()
    }
}