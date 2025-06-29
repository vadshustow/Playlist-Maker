package com.practicum.playlistmaker.search.data.repository

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.domain.repository.SearchHistoryRepository

const val TRACK_LIST_KEY = "track_list_key"
const val HISTORY_SIZE = 10

class SearchHistoryRepositoryImpl(private val sharedPrefs: SharedPreferences) : SearchHistoryRepository{

    private val historyTracksList = readSearchHistory()

    override fun addTrackToSearchHistory(track: Track) {

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

    override fun getHistoryList(): List<Track> {
        return historyTracksList
    }

    override fun clearSearchHistory() {
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