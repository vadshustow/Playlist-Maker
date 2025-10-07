package com.practicum.playlistmaker.library.playlist.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String?,
    val coverImagePath: String,
    val trackIdsJson: String,
    val trackCount: Int
)
