package com.practicum.playlistmaker.library.favorite.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.library.favorite.data.db.TrackDao
import com.practicum.playlistmaker.library.favorite.data.db.TrackEntity
import com.practicum.playlistmaker.library.playlist.data.db.PlaylistDao
import com.practicum.playlistmaker.library.playlist.data.db.PlaylistEntity
import com.practicum.playlistmaker.library.playlist.data.db.PlaylistTrackDao
import com.practicum.playlistmaker.library.playlist.data.db.PlaylistTrackEntity

@Database(
    version = 4,
    entities = [
        TrackEntity::class,
        PlaylistEntity::class,
        PlaylistTrackEntity::class
               ],
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao

    abstract fun playlistDao(): PlaylistDao

    abstract fun playlistTrackDao(): PlaylistTrackDao
}