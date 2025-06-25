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

    private val _trackList = MutableLiveData<List<Track>>()
    val trackList: LiveData<List<Track>> get() = _trackList

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _errorMessage = MutableLiveData<Pair<String, Int>>() // Text, Drawable
    val errorMessage: LiveData<Pair<String, Int>> get() = _errorMessage

    private val _historyList = MutableLiveData<List<Track>>()
    val historyList: LiveData<List<Track>> get() = _historyList

    fun search(term: String) {
        _loading.postValue(true)
        tracksInteractor.searchTrack(term, object : Consumer<List<Track>> {
            override fun consume(data: ConsumerData<List<Track>>) {
                _loading.postValue(false)
                when (data) {
                    is ConsumerData.Data -> {
                        if (data.data.isEmpty()) {
                            val text = getApplication<Application>().getString(R.string.nothing_found)
                            _errorMessage.postValue(text to R.drawable.ic_nothing_found)
                        } else {
                            _trackList.postValue(data.data)
                        }
                    }
                    is ConsumerData.Error -> {
                        val text = getApplication<Application>().getString(R.string.connection_error)
                        _errorMessage.postValue(text to R.drawable.ic_connection_error)
                    }
                }
            }
        })
    }

    fun loadHistory() {
        _historyList.postValue(searchHistoryInteractor.getHistoryList())
    }

    fun addTrackToHistory(track: Track) {
        searchHistoryInteractor.addTrackToSearchHistory(track)
        loadHistory()
    }

    fun clearHistory() {
        searchHistoryInteractor.clearSearchHistory()
        loadHistory()
    }

    fun clearSearchResults() {
        _trackList.postValue(emptyList())
    }
}