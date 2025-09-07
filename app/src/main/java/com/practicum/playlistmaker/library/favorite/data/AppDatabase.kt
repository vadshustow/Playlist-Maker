package com.practicum.playlistmaker.library.favorite.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.library.favorite.data.db.TrackDao
import com.practicum.playlistmaker.library.favorite.data.db.TrackEntity

@Database(version = 1, entities = [TrackEntity::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao
}