package com.geniusscansdk.demo.utils

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import coil3.size.Precision
import coil3.size.Scale
import coil3.toBitmap
import java.io.File

@Composable
fun rememberBitmap(
    imageFile: File,
    targetSize: IntSize,
    contentScale: ContentScale = ContentScale.Fit,
): ImageBitmap? {
    val context = LocalContext.current.applicationContext
    val imageLoader = remember(context) { ImageLoader.Builder(context).build() }
    val bitmap by produceState<Bitmap?>(
        initialValue = null,
        imageFile,
        targetSize,
        contentScale,
        imageLoader,
    ) {
        if (targetSize.width <= 0 || targetSize.height <= 0) {
            value = null
            return@produceState
        }

        val request = ImageRequest.Builder(context)
            .data(imageFile)
            .size(targetSize.width, targetSize.height)
            .scale(contentScale.toCoilScale())
            .precision(Precision.INEXACT)
            .allowHardware(false)
            .build()
        value = (imageLoader.execute(request) as? SuccessResult)?.image?.toBitmap()
    }
    return bitmap?.asImageBitmap()
}

private fun ContentScale.toCoilScale(): Scale = when (this) {
    ContentScale.Crop -> Scale.FILL
    ContentScale.Fit -> Scale.FIT
    else -> throw IllegalArgumentException("ContentScale $this is not supported")
}
