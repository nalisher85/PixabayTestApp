package com.example.pixabaytest.data.network

import com.example.pixabaytest.data.network.models.ImageSearchResultData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ImageApi {

    @GET("api")
    suspend fun loadImageByQuery(
        @Query("q") query: String,
        @Query("key") apiPey: String, //todo move to interceptor
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Response<ImageSearchResultData>
}