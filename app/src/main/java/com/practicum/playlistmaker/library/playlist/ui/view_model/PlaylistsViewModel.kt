package com.practicum.playlistmaker.library.playlist.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.playlist.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.library.playlist.ui.PlaylistState
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistInteractor: PlaylistInteractor,
) : ViewModel() {

    private val _playlistState = MutableLiveData<PlaylistState>()
    val playlistState: LiveData<PlaylistState> = _playlistState

    init {
        getPlaylists()
    }

    fun getPlaylists() {
        viewModelScope.launch {
            playlistInteractor
                .getAllPlaylists()
                .collect { playlists ->
                    if (playlists.isEmpty()) {
                        _playlistState.postValue(PlaylistState.Empty)
                    } else {
                        _playlistState.postValue(PlaylistState.Content(playlists))
                    }
                }
        }
    }
}