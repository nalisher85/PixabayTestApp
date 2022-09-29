package com.example.pixabaytest.data.network


import androidx.annotation.Keep
import retrofit2.http.Field

@Keep
data class HitData(
    val comments: Int = 0,
    val downloads: Int = 0,
    val fullHDURL: String = "",
    val id: Int = 0,
    val imageHeight: Int = 0,
    val imageSize: Int = 0,
    val imageURL: String = "",
    val imageWidth: Int = 0,
    val largeImageURL: String = "",
    val likes: Int = 0,
    val pageURL: String = "",
    val previewHeight: Int = 0,
    val previewURL: String = "",
    val previewWidth: Int = 0,
    val tags: String = "",
    val type: String = "",
    val user: String = "",
    val userImageURL: String = "",
    @Field("user_id") val userId: Int = 0,
    val views: Int = 0,
    val webformatHeight: Int = 0,
    val webformatURL: String = "",
    val webformatWidth: Int = 0
)