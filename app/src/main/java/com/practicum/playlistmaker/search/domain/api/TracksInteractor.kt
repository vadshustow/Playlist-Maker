package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.consumer.Consumer
import com.practicum.playlistmaker.search.domain.model.Track

interface TracksInteractor {
    fun searchTrack(term: String, consumer: Consumer<List<Track>>)
}