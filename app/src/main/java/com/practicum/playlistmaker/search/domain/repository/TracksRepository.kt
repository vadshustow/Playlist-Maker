package com.practicum.playlistmaker.search.domain.repository

import com.practicum.playlistmaker.search.domain.model.Resource
import com.practicum.playlistmaker.search.domain.model.Track

interface TracksRepository {
    fun searchTracks(term: String): Resource<List<Track>>
}