package com.practicum.playlistmaker.search.domain.consumer

interface ConsumerData<T> {
    data class Data<T>(val data: T) : ConsumerData<T>
    data class Error<T>(val message: Int) : ConsumerData<T>
}