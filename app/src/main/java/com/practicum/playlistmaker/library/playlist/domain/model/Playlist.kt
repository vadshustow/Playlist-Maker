package com.practicum.playlistmaker.library.playlist.domain.model

data class Playlist(
    val id: Int = 0,
    val name: String,
    val description: String?,
    val coverImagePath: String,
    val trackIds: List<Int>,
    val trackCount: Int,
)
