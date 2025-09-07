package com.practicum.playlistmaker.search.ui.view_model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.ui.SearchState
import kotlinx.coroutines.launch

class SearchViewModel(
    private val application: Application,
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor,
) : ViewModel() {

    private val _searchState = MutableLiveData<SearchState>()
    val searchState: LiveData<SearchState> get() = _searchState

    fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {

            renderState(SearchState.Loading)

            viewModelScope.launch {
                tracksInteractor
                    .searchTracks(newSearchText)
                    .collect { pair ->
                        processResult(pair.first, pair.second)
                    }
            }
        }
    }

    private fun processResult(foundTracks: List<Track>?, errorMessage: String?) {

        val tracks = mutableListOf<Track>()
        if (foundTracks != null) {
            tracks.addAll(foundTracks)
        }

        when {
            errorMessage != null -> {
                val text = application.getString(R.string.connection_error)
                renderState(SearchState.Error(text, R.drawable.ic_connection_error))
            }

            tracks.isEmpty() -> {
                val text = application.getString(R.string.nothing_found)
                renderState(SearchState.Error(text, R.drawable.ic_nothing_found))
            }

            else -> {
                renderState(SearchState.Tracks(tracks))
            }
        }
    }

    fun loadHistory() {
        viewModelScope.launch {
            _searchState.postValue(SearchState.History(searchHistoryInteractor.getHistoryList()))
        }
    }

    fun addTrackToHistory(track: Track) {
        searchHistoryInteractor.addTrackToSearchHistory(track)
        if (_searchState.value is SearchState.History) {
            loadHistory()
        }
    }

    fun clearHistory() {
        searchHistoryInteractor.clearSearchHistory()
        loadHistory()
    }

    fun clearSearchResults() {
        _searchState.postValue(SearchState.Tracks(emptyList()))
    }

    private fun renderState(state: SearchState) {
        _searchState.postValue(state)
    }
}