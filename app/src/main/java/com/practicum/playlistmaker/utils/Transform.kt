package com.practicum.playlistmaker.utils

import android.content.Context
import android.util.TypedValue

object Transform {

    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }

    fun getTracksCountText(count: Int): String {
        val mod10 = count % 10
        val mod100 = count % 100

        val word = when {
            mod10 == 1 && mod100 != 11 -> "трек"
            mod10 in 2..4 && (mod100 !in 12..14) -> "трека"
            else -> "треков"
        }

        return "$count $word"
    }

    fun getMinutesText(minutes: Int): String {
        val mod10 = minutes % 10
        val mod100 = minutes % 100

        val word = when {
            mod10 == 1 && mod100 != 11 -> "минута"
            mod10 in 2..4 && (mod100 !in 12..14) -> "минуты"
            else -> "минут"
        }

        return "$minutes $word"
    }
}