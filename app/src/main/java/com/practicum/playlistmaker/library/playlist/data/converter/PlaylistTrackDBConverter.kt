package com.practicum.playlistmaker.library.playlist.data.converter

import com.practicum.playlistmaker.library.playlist.data.db.PlaylistTrackEntity
import com.practicum.playlistmaker.search.domain.model.Track

class PlaylistTrackDBConverter {
    fun map(track: Track): PlaylistTrackEntity {
        return PlaylistTrackEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl,
        )
    }
}