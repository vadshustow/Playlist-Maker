package com.practicum.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import androidx.room.Room
import com.google.gson.Gson
import com.practicum.playlistmaker.library.favorite.data.AppDatabase
import com.practicum.playlistmaker.library.favorite.data.converter.TrackDBConverter
import com.practicum.playlistmaker.library.playlist.data.converter.PlaylistDBConverter
import com.practicum.playlistmaker.library.playlist.data.converter.PlaylistTrackDBConverter
import com.practicum.playlistmaker.search.data.network.ItunesApiService
import com.practicum.playlistmaker.search.data.network.NetworkClient
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.utils.PM_PREFERENCES
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<ItunesApiService> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesApiService::class.java)
    }

    factory { Gson() }

    factory { MediaPlayer() }

    factory { TrackDBConverter() }

    factory { PlaylistDBConverter(get()) }

    factory { PlaylistTrackDBConverter() }

    single {
        androidContext().getSharedPreferences(PM_PREFERENCES, Context.MODE_PRIVATE)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get(), androidContext())
    }

    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "database.db"
        )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }
}