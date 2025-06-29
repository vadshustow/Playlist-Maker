package com.practicum.playlistmaker.search.data.dto

import android.icu.text.SimpleDateFormat
import java.util.Locale

data class TrackDto(
    val trackId: Int?,
    val trackName: String?,
    val artistName: String?,
    val trackTimeMillis: Long?,
    val artworkUrl100: String?,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String,
) {

    fun getTrackTime(): String =
        SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)

    fun getReleaseYear(): String {
        val parseDate =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).parse(releaseDate)
        return SimpleDateFormat("yyyy", Locale.getDefault()).format(parseDate)
    }
}
