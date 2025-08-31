package com.practicum.playlistmaker.player.ui.view_model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.library.favorite.domain.api.FavoriteTracksInteractor
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.ui.AudioPlayerScreenState
import com.practicum.playlistmaker.player.ui.AudioPlayerState
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel(
    private val application: Application,
    private val playerInteractor: PlayerInteractor,
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
    private val url: String,
    private val track: Track,
) : ViewModel() {

    private val _audioPlayerScreenState = MutableLiveData(
        AudioPlayerScreenState(
            playerState = AudioPlayerState.DEFAULT,
            progressTime = application.getString(R.string.def_progress_time)
        )
    )
    val audioPlayerScreenState: LiveData<AudioPlayerScreenState> = _audioPlayerScreenState

    private val _isFavorite = MutableLiveData(track.isFavorite)
    val isFavorite: LiveData<Boolean> get() = _isFavorite

    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            val favorite = favoriteTracksInteractor.isTrackFavorite(track.trackId)
            _isFavorite.postValue(favorite)
        }
        preparePlayer()
    }

    fun onFavoriteClicked() {
        viewModelScope.launch {
            val nowFavorite = _isFavorite.value ?: false
            if (nowFavorite) {
                favoriteTracksInteractor.deleteTrackFromFavorite(track)
            } else {
                favoriteTracksInteractor.addTrackToFavorite(track)
            }
            _isFavorite.postValue(!nowFavorite)
        }
    }


    private fun preparePlayer() {
        playerInteractor.preparePlayer(
            url = url,
            onPrepared = {
                _audioPlayerScreenState.value =
                    _audioPlayerScreenState.value?.copy(playerState = AudioPlayerState.PREPARED)
            },
            onCompletion = {
                _audioPlayerScreenState.value =
                    _audioPlayerScreenState.value?.copy(playerState = AudioPlayerState.PREPARED)
                resetTimer()
            }
        )
    }

    fun onPlayButtonClicked() {
        when (_audioPlayerScreenState.value?.playerState) {
            AudioPlayerState.PLAYING -> pausePlayer()
            AudioPlayerState.PREPARED, AudioPlayerState.PAUSED -> startPlayer()
            else -> {}
        }
    }

    private fun startPlayer() {
        playerInteractor.startPlayer()
        _audioPlayerScreenState.value =
            _audioPlayerScreenState.value?.copy(playerState = AudioPlayerState.PLAYING)
        startTimer()
    }

    private fun pausePlayer() {
        playerInteractor.pausePlayer()
        _audioPlayerScreenState.value =
            _audioPlayerScreenState.value?.copy(playerState = AudioPlayerState.PAUSED)
        timerJob?.cancel()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (playerInteractor.isPlaying()) {
                _audioPlayerScreenState.value =
                    _audioPlayerScreenState.value?.copy(progressTime = getCurrentPlayerPosition())
                delay(DELAY)
            }
        }
    }

    private fun resetTimer() {
        timerJob?.cancel()
        timerJob = null
        _audioPlayerScreenState.value =
            _audioPlayerScreenState.value?.copy(progressTime = application.getString(R.string.def_progress_time))
    }

    fun onPause() {
        pausePlayer()
    }

    override fun onCleared() {
        super.onCleared()
        resetTimer()
        playerInteractor.release()
    }

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        ).format(playerInteractor.getCurrentPosition())
    }

    companion object {
        private const val DELAY = 300L
    }
}