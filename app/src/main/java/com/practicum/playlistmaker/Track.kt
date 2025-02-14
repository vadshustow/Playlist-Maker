package com.practicum.playlistmaker

import android.icu.text.SimpleDateFormat
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Locale

@Parcelize
data class Track(
    val trackId: Int?,
    val trackName: String?,
    val artistName: String?,
    val trackTimeMillis: Long?,
    val artworkUrl100: String?,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?
) : Parcelable {

    fun getCoverArtwork() = artworkUrl100?.replaceAfterLast('/',"512x512bb.jpg")

    fun getTrackTime(): String = SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)

    fun getReleaseYear(): String {
        val parseDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).parse(releaseDate)
        return SimpleDateFormat("yyyy", Locale.getDefault()).format(parseDate)
    }
}
