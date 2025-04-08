package com.practicum.playlistmaker.domain.repository

interface PlayerRepository {
    fun preparePlayer(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun release()
    fun isPlaying(): Boolean
    fun getCurrentPosition(): Int
}