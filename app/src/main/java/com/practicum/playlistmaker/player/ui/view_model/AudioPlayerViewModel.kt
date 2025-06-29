package com.practicum.playlistmaker.player.ui.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.ui.AudioPlayerScreenState
import com.practicum.playlistmaker.player.ui.AudioPlayerState
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val url: String,
) : ViewModel() {

    companion object {

        private const val DELAY = 500L

        fun getFactory(url: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AudioPlayerViewModel(
                    Creator.providePlayerInteractor(),
                    url
                )
            }
        }
    }

    private val _audioPlayerScreenState = MutableLiveData(
        AudioPlayerScreenState(
            playerState = AudioPlayerState.DEFAULT,
            progressTime = "00:00"
        )
    )
    val audioPlayerScreenState: LiveData<AudioPlayerScreenState> = _audioPlayerScreenState

    private val handler = Handler(Looper.getMainLooper())

    private val timerRunnable = object : Runnable {
        override fun run() {
            if (playerInteractor.isPlaying()) {
                val formattedTime = SimpleDateFormat(
                    "mm:ss",
                    Locale.getDefault()
                ).format(playerInteractor.getCurrentPosition())
                _audioPlayerScreenState.value =
                    _audioPlayerScreenState.value?.copy(progressTime = formattedTime)
                handler.postDelayed(this, DELAY)
            }
        }
    }

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
        _audioPlayerScreenState.value = _audioPlayerScreenState.value?.copy(playerState = AudioPlayerState.PLAYING)
        handler.post(timerRunnable)
    }

    private fun pausePlayer() {
        playerInteractor.pausePlayer()
        _audioPlayerScreenState.value = _audioPlayerScreenState.value?.copy(playerState = AudioPlayerState.PAUSED)
        handler.removeCallbacks(timerRunnable)
    }

    private fun resetTimer() {
        handler.removeCallbacks(timerRunnable)
        _audioPlayerScreenState.value = _audioPlayerScreenState.value?.copy(progressTime = "00:00")
    }

    fun onPause() {
        pausePlayer()
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(timerRunnable)
        playerInteractor.release()
    }

}