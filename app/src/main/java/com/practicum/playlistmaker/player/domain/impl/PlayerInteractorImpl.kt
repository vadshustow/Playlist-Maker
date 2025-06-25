package com.practicum.playlistmaker.player.domain.impl

import com.practicum.playlistmaker.player.domain.repository.PlayerRepository
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor

class PlayerInteractorImpl(private val repository: PlayerRepository) : PlayerInteractor {
    override fun preparePlayer(
        url: String,
        onPrepared: () -> Unit,
        onCompletion: () -> Unit,
    ) {
        repository.preparePlayer(url, onPrepared, onCompletion)
    }

    override fun startPlayer() {
        repository.startPlayer()
    }

    override fun pausePlayer() {
        repository.pausePlayer()
    }

    override fun release() {
        repository.release()
    }

    override fun isPlaying(): Boolean {
        return repository.isPlaying()
    }

    override fun getCurrentPosition(): Int {
        return repository.getCurrentPosition()
    }
}