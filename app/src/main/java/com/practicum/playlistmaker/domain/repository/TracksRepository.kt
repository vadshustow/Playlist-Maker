package com.practicum.playlistmaker.domain.repository

import com.practicum.playlistmaker.domain.model.Resource
import com.practicum.playlistmaker.domain.model.Track

interface TracksRepository {
    fun searchTracks(term: String): Resource<List<Track>>
}