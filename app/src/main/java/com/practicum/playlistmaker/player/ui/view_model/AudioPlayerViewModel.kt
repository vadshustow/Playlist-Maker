package com.practicum.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.ui.AudioPlayerScreenState
import com.practicum.playlistmaker.player.ui.AudioPlayerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val url: String,
) : ViewModel() {

    private val _audioPlayerScreenState = MutableLiveData(
        AudioPlayerScreenState(
            playerState = AudioPlayerState.DEFAULT,
            progressTime = "00:00"
        )
    )
    val audioPlayerScreenState: LiveData<AudioPlayerScreenState> = _audioPlayerScreenState

    private var timerJob: Job? = null

    init {
        preparePlayer()
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
            _audioPlayerScreenState.value?.copy(progressTime = "00:00")
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