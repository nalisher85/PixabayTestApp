package com.example.pixabaytest.data.network.models


import androidx.annotation.Keep

@Keep
data class ImageSearchResultData(
    val hits: List<HitData> = listOf(),
    val total: Int = 0,
    val totalHits: Int = 0
)