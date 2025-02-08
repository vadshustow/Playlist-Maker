package com.practicum.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

const val TRACK_LIST_KEY = "track_list_key"
const val HISTORY_SIZE = 10

class SearchHistory(private val sharedPrefs: SharedPreferences) {

    private val historyTracksList = readSearchHistory()

    fun addTrackToSearchHistory(track: Track) {

        val iterator = historyTracksList.iterator()

        while (iterator.hasNext()) {
            if (iterator.next().trackId == track.trackId) {
                iterator.remove()
            }
        }

        if (historyTracksList.size >= HISTORY_SIZE) {
            historyTracksList.removeAt(historyTracksList.lastIndex)
            historyTracksList.add(0, track)
            writeSearchHistory(historyTracksList)
            return
        } else {
            historyTracksList.add(0, track)
            writeSearchHistory(historyTracksList)
            return
        }
    }

    fun getHistoryList(): List<Track> {
        return historyTracksList
    }

    fun clearSearchHistory() {
        historyTracksList.clear()
        writeSearchHistory(historyTracksList)
    }

    private fun readSearchHistory(): ArrayList<Track> {
        val json = sharedPrefs.getString(TRACK_LIST_KEY, null) ?: return ArrayList()
        val type = object : TypeToken<ArrayList<Track>>() {}.type
        return Gson().fromJson(json, type)
    }

    private fun writeSearchHistory(tracks: ArrayList<Track>) {
        val json = Gson().toJson(tracks)
        sharedPrefs.edit()
            .putString(TRACK_LIST_KEY, json)
            .apply()
    }
}