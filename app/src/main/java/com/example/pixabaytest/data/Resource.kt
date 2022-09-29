package com.example.pixabaytest.data

import com.example.pixabaytest.data.network.models.ImageSearchResultData

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Loading<T>(data: T? = null): Resource<T>(data)
    class Error<T>(data: T? = null, message: String?): Resource<T>(data, message)
    class Success<T>(data: T?): Resource<T>(data)
    object Empty: Resource<ImageSearchResultData>()
}