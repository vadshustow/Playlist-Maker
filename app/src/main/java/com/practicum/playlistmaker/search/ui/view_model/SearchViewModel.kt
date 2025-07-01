package com.practicum.playlistmaker.search.ui.view_model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.consumer.Consumer
import com.practicum.playlistmaker.search.domain.consumer.ConsumerData
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.ui.SearchState

class SearchViewModel(
    private val application: Application,
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor,
) : ViewModel() {

    private val _searchState = MutableLiveData<SearchState>()
    val searchState: LiveData<SearchState> get() = _searchState

    fun search(term: String) {
        _searchState.postValue(SearchState.Loading)
        tracksInteractor.searchTrack(term, object : Consumer<List<Track>> {
            override fun consume(data: ConsumerData<List<Track>>) {
                when (data) {
                    is ConsumerData.Data -> {
                        if (data.data.isEmpty()) {
                            val text = application.getString(R.string.nothing_found)
                            _searchState.postValue(SearchState.Error(text, R.drawable.ic_nothing_found))
                        } else {
                            _searchState.postValue(SearchState.Tracks(data.data))
                        }
                    }
                    is ConsumerData.Error -> {
                        val text = application.getString(R.string.connection_error)
                        _searchState.postValue(SearchState.Error(text, R.drawable.ic_connection_error))
                    }
                }
            }
        })
    }

    fun loadHistory() {
        _searchState.postValue(SearchState.History(searchHistoryInteractor.getHistoryList()))
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
}