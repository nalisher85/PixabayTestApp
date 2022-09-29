package com.example.pixabaytest.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pixabaytest.data.Repository
import com.example.pixabaytest.data.Resource
import com.example.pixabaytest.data.network.Constants.perPageCount
import com.example.pixabaytest.data.network.ImageSearchResultData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class QueryImagesViewModel @Inject constructor(
    private val repository: Repository
): ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    val onQuery: (String) -> Unit = { q ->
        _query.value = q
    }

    val onNextPage: () -> Unit = onNextPage@ {
        if (_uiState.value.isLoading || _uiState.value.endReached) return@onNextPage
        _uiState.value = _uiState.value.copy(page = _uiState.value.page + 1)
    }


    init {

        viewModelScope.launch {
            _query
                .filterNot { it.isBlank() }
                .debounce(1000)
                .onEach {
                    _uiState.value = UiState(isLoading = true)
                }.flatMapLatest { query ->
                    repository.getImageByQuery(query)
                }.collectLatest { res ->
                    performResult(res)
                }
        }

        viewModelScope.launch {
            _uiState.distinctUntilChangedBy { it.page }
                .dropWhile { _query.value.isBlank() }
                .flatMapLatest { state ->
                    repository.getImageByQuery(_query.value, state.page, perPageCount)
                }.collectLatest { performResult(it) }
        }

    }

    private fun performResult(result: Resource<ImageSearchResultData>) {
        when(result) {
            is Resource.Loading -> {
                _uiState.value = _uiState.value.copy(
                    error = null,
                    isLoading = true,
                )
            }
            is Resource.Error -> {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result.message,
                )
            }
            is Resource.Success -> {
                _uiState.value = _uiState.value.copy(
                    error = null,
                    isLoading = false,
                    uiData = convertDataModelToUiModel(result.data!!, _uiState.value.uiData?.images ?: emptyList()),
                    endReached = result.data.hits.isEmpty()
                )
            }
            is Resource.Empty -> {
                _uiState.value = _uiState.value.copy(
                    error = null,
                    isLoading = false,
                    endReached = true
                )
            }
        }
    }

    private fun convertDataModelToUiModel(
        dataModel: ImageSearchResultData,
        initialList: List<ImageModel> = emptyList()
    ): UiModel {
        return UiModel(
            total = dataModel.total,
            totalLoaded = dataModel.totalHits,
            images = initialList + dataModel.hits.map { hitData ->
            ImageModel(id = hitData.id, imageUrl = hitData.largeImageURL, likes = hitData.likes, user = hitData.user)
        })
    }





    data class UiState(
        val error: String? = null,
        val isLoading: Boolean = false,
        val uiData: UiModel? = null,
        val page: Int = 1,
        val endReached: Boolean = false
    )

    data class UiModel(
        val total: Int,
        val totalLoaded: Int,
        val images: List<ImageModel> = emptyList()
    )

    data class ImageModel(
        val id: Int,
        val imageUrl: String,
        val likes: Int,
        val user: String
    )
}