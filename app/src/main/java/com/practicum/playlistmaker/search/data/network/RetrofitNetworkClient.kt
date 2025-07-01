package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.search.data.dto.Response
import com.practicum.playlistmaker.search.data.dto.TracksSearchRequest

class RetrofitNetworkClient(private val itunesApiService: ItunesApiService) : NetworkClient {

    override fun doRequest(dto: Any): Response {
        if (dto is TracksSearchRequest) {
            try {
                val resp = itunesApiService.search(dto.term).execute()

                val body = resp.body() ?: Response()

                return body.apply { resultCode = resp.code() }
            } catch (e: Exception) {
                return Response().apply { resultCode = 400 }
            }
        } else {
            return Response().apply { resultCode = 400 }
        }
    }
}