package com.practicum.playlistmaker.library.playlist.data.converter

import com.google.gson.Gson
import com.practicum.playlistmaker.library.playlist.data.db.PlaylistEntity
import com.practicum.playlistmaker.library.playlist.domain.model.Playlist

class PlaylistDBConverter(private val gson: Gson) {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            coverImagePath = playlist.coverImagePath,
            trackIdsJson = gson.toJson(playlist.trackIds),
            trackCount = playlist.trackCount
        )
    }

    fun map(playlistEntity: PlaylistEntity): Playlist {
        val trackIds: List<Int> = gson.fromJson(playlistEntity.trackIdsJson, Array<Int>::class.java).toList()
        return Playlist(
            id = playlistEntity.id,
            name = playlistEntity.name,
            description = playlistEntity.description,
            coverImagePath = playlistEntity.coverImagePath,
            trackIds = trackIds,
            trackCount = playlistEntity.trackCount
        )
    }
}