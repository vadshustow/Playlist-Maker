package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.consumer.Consumer
import com.practicum.playlistmaker.search.domain.consumer.ConsumerData
import com.practicum.playlistmaker.search.domain.model.Resource
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.domain.repository.TracksRepository
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTrack(term: String, consumer: Consumer<List<Track>>) {
        executor.execute {
            val response = repository.searchTracks(term)

            when (response) {
                is Resource.Success -> {
                    consumer.consume(ConsumerData.Data(response.data))
                }

                is Resource.Error -> {
                    consumer.consume(ConsumerData.Error(response.message))
                }
            }
        }
    }
}