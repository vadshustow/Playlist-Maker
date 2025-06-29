package com.practicum.playlistmaker.search.domain.model

interface Resource<T> {
    data class Success<T>(val data: T) : Resource<T>
    data class Error<T>(val message: Int) : Resource<T>
}