package com.example.pixabaytest.data

import android.util.Log
import androidx.compose.ui.unit.Constraints
import com.example.pixabaytest.data.network.Constants
import com.example.pixabaytest.data.network.ImageApi
import com.example.pixabaytest.data.network.ImageSearchResultData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class Repository @Inject constructor(
    private val imageApi: ImageApi
) {
    fun getImageByQuery(query: String, page: Int = 1, perPage: Int = 20): Flow<Resource<ImageSearchResultData>> {
        return flow {
            emit(Resource.Loading())
            val response = imageApi.loadImageByQuery(query, Constants.apiKey, page, perPage)

            when {
                !response.isSuccessful -> emit(Resource.Error(message = "${response.code()} ${response.message()}")
                )
                response.body() == null || response.body()?.hits.isNullOrEmpty() -> emit(Resource.Empty)
                else -> emit(Resource.Success(data = response.body()))
            }
        }
    }
}