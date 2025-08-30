package com.practicum.playlistmaker.search.data.repository

import com.practicum.playlistmaker.search.data.network.NetworkClient
import com.practicum.playlistmaker.search.data.dto.TracksSearchRequest
import com.practicum.playlistmaker.search.data.dto.TracksSearchResponse
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.domain.repository.TracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(term: String): Flow<Result<List<Track>>> = flow {

        val response = networkClient.doRequest(TracksSearchRequest(term))

        when (response.resultCode) {
            -1 -> {
                emit(Result.failure(Exception("Проверьте подключение к интернету")))
            }

            200 -> {
                with(response as TracksSearchResponse) {
                    val data = results.map {
                        Track(
                            it.trackId,
                            it.trackName,
                            it.artistName,
                            it.getTrackTime(),
                            it.artworkUrl100,
                            it.collectionName,
                            it.getReleaseYear(),
                            it.primaryGenreName,
                            it.country,
                            it.previewUrl
                        )
                    }
                    emit(Result.success(data))
                }
            }

            else -> emit(Result.failure(Exception("Ошибка сервера")))
        }
    }
}