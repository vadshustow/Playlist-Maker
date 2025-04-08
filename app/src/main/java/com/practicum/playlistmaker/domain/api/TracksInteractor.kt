package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.consumer.Consumer
import com.practicum.playlistmaker.domain.model.Track

interface TracksInteractor {
    fun searchTrack(term: String, consumer: Consumer<List<Track>>)
}