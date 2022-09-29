package com.example.pixabaytest.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import com.example.pixabaytest.R

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SearchImageScreen(viewModel: QueryImagesViewModel) {
    val uiState = viewModel.uiState.collectAsState()
    val query = viewModel.query.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                backgroundColor = Color.White,
                contentColor = Color.Black
            ) {
                TextField(
                    value = query.value,
                    onValueChange = viewModel.onQuery,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    ) {
        
        when {
            uiState.value.isLoading && uiState.value.uiData?.images.isNullOrEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {                 
                    CircularProgressIndicator()
                }
            }
            uiState.value.error != null && uiState.value.uiData?.images.isNullOrEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = uiState.value.error!!)
                }
            }
            uiState.value.uiData != null -> {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(text = "total image ${uiState.value.uiData?.total}")
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "total loaded ${uiState.value.uiData?.totalLoaded}")
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(uiState.value.uiData?.images?.size ?: 0) { i ->
                            val item = uiState.value.uiData!!.images[i]
                            if (i >= uiState.value.uiData!!.images.lastIndex) {
                                viewModel.onNextPage()
                            }

                            ImageItem(item = item)
                        }

                        item {
                            uiState.value.error?.let {
                                Text(text = it)
                            }
                        }
                        item {
                            if (uiState.value.isLoading) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ImageItem(item: QueryImagesViewModel.ImageModel) {

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SubcomposeAsyncImage(
            model = item.imageUrl,
            contentDescription = "",

            ) {
            when(val state = painter.state) {
                AsyncImagePainter.State.Empty -> {}
                is AsyncImagePainter.State.Error -> {
                    Text(text = (painter.state as? AsyncImagePainter.State.Error)
                        ?.result?.throwable?.localizedMessage ?: "Error loading image",
                        modifier = Modifier.padding(vertical = 10.dp))
                }
                is AsyncImagePainter.State.Loading ->
                    Text(
                        text = "Loading... ",
                        modifier = Modifier.padding(vertical = 20.dp)
                    )
                is AsyncImagePainter.State.Success -> {
                    Box(contentAlignment = Alignment.Center) {
                        Image(painter = state.painter, contentDescription = "")

                    }
                }
            }
        }

        Text(text = item.user, modifier = Modifier.padding(bottom = 10.dp))
        Row {
            Icon(modifier = Modifier.size(20.dp), painter = painterResource(id = R.drawable.thumb_up), contentDescription = "")
            Text(text = item.likes.toString(), modifier = Modifier.padding(start = 10.dp))
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}