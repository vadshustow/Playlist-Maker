package com.practicum.playlistmaker.library.favorite.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.favorite.domain.api.FavoriteTracksInteractor
import com.practicum.playlistmaker.library.favorite.ui.FavoriteTracksState
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
) : ViewModel() {

    private val _favoriteTracksState = MutableLiveData<FavoriteTracksState>()
    val favoriteTracksState: LiveData<FavoriteTracksState> = _favoriteTracksState

    init {
        loadFavoriteTracks()
    }

    fun loadFavoriteTracks() {
        viewModelScope.launch {
            favoriteTracksInteractor.getAllFavoriteTracks()
                .collect { tracks ->
                    if (tracks.isEmpty()) {
                        _favoriteTracksState.postValue(FavoriteTracksState.Empty)
                    } else {
                        _favoriteTracksState.postValue(FavoriteTracksState.Content(tracks))
                    }
                }
        }
    }
}