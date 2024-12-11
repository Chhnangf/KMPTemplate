package com.kashif.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.kashif.common.charts.BarChartView
import com.kashif.common.charts.model.ChartDataSet
import com.kashif.common.charts.style.BarChartDefaults
import com.seiko.imageloader.ImageRequestState
import com.seiko.imageloader.rememberAsyncImagePainter

@Composable
internal fun App(platform: String) {
    var text by remember { mutableStateOf("Hello, World!") }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround) {
            val items: List<Float> = listOf(100f, -60f, 0f, -90f, 40f, 80f)
            BarChartView(
                dataSet = ChartDataSet(
                    items = items,
                    title = "Emotional",
                ),
                style = BarChartDefaults.style(
                    space = 2.dp,
                )
            )
                Button(onClick = { text = "Hello, $platform" }) { Text(text) }
            }
    }
}

@Composable
internal fun AsyncImage(url: String, modifier: Modifier) {

    val painter = rememberAsyncImagePainter(url = url)
    Image(
        painter = painter,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier,
    )
    when (val requestState = painter.requestState) {
       is  ImageRequestState.Loading -> {
            Box(modifier = modifier, contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is ImageRequestState.Failure -> {
            Text(requestState.error.message ?: "Error")
        }
        ImageRequestState.Success -> Unit
    }
}
