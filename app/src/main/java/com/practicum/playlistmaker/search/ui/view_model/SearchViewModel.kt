package com.practicum.playlistmaker.search.ui.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.consumer.Consumer
import com.practicum.playlistmaker.search.domain.consumer.ConsumerData
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.ui.SearchState

class SearchViewModel(
    application: Application,
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor,
) : AndroidViewModel(application) {

    companion object {
        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(
                    this[APPLICATION_KEY] as Application,
                    Creator.provideTracksInteractor(),
                    Creator.provideSearchHistoryInteractor()
                )
            }
        }
    }

    private val _searchState = MutableLiveData<SearchState>()
    val searchState: LiveData<SearchState> get() = _searchState

    fun search(term: String) {
        _searchState.postValue(SearchState.Loading)
        tracksInteractor.searchTrack(term, object : Consumer<List<Track>> {
            override fun consume(data: ConsumerData<List<Track>>) {
                when (data) {
                    is ConsumerData.Data -> {
                        if (data.data.isEmpty()) {
                            val text = getApplication<Application>().getString(R.string.nothing_found)
                            _searchState.postValue(SearchState.Error(text, R.drawable.ic_nothing_found))
                        } else {
                            _searchState.postValue(SearchState.Tracks(data.data))
                        }
                    }
                    is ConsumerData.Error -> {
                        val text = getApplication<Application>().getString(R.string.connection_error)
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