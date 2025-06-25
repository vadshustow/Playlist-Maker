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
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val url: String,
) : ViewModel() {

    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
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

    private val playerStateLiveData = MutableLiveData(STATE_DEFAULT)
    fun observePlayerState(): LiveData<Int> = playerStateLiveData

    private val progressTimeLiveData = MutableLiveData("00:00")
    fun observeProgressTime(): LiveData<String> = progressTimeLiveData

    private val handler = Handler(Looper.getMainLooper())

    private val timerRunnable = object : Runnable {
        override fun run() {
            if (playerInteractor.isPlaying()) {
                val formattedTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(playerInteractor.getCurrentPosition())
                progressTimeLiveData.postValue(formattedTime)
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
                playerStateLiveData.postValue(STATE_PREPARED)
            },
            onCompletion = {
                playerStateLiveData.postValue(STATE_PREPARED)
                resetTimer()
            }
        )
    }

    fun onPlayButtonClicked() {
        when (playerStateLiveData.value) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    private fun startPlayer() {
        playerInteractor.startPlayer()
        playerStateLiveData.postValue(STATE_PLAYING)
        handler.post(timerRunnable)
    }

    private fun pausePlayer() {
        playerInteractor.pausePlayer()
        playerStateLiveData.postValue(STATE_PAUSED)
        handler.removeCallbacks(timerRunnable)
    }

    private fun resetTimer() {
        handler.removeCallbacks(timerRunnable)
        progressTimeLiveData.postValue("00:00")
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