package com.geniusscansdk.demo.processing

import android.graphics.BitmapFactory
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.geniusscansdk.core.DocumentDetector
import com.geniusscansdk.core.FileInput
import com.geniusscansdk.core.GeniusScanSDK
import com.geniusscansdk.core.Quadrangle
import com.geniusscansdk.demo.R
import com.geniusscansdk.demo.model.Page
import com.geniusscansdk.ui.compose.EditFrameView
import com.geniusscansdk.ui.compose.rememberEditFrameViewState
import com.geniusscansdk.util.ExperimentalApi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class, ExperimentalApi::class)
@Composable
fun EditFrameScreen(
    page: Page,
    onConfirmed: () -> Unit,
) {
    val context = LocalContext.current.applicationContext
    val editFrameState = rememberEditFrameViewState(
        page.quadrangle ?: Quadrangle.createFullQuadrangle(),
    )
    val imageBitmap = remember(page.originalImage) {
        BitmapFactory.decodeFile(page.originalImage.absolutePath).asImageBitmap()
    }

    LaunchedEffect(page.originalImage) {
        if (!editFrameState.quadrangle.isFullImage()) return@LaunchedEffect
        try {
            val quadrangle = withContext(Dispatchers.IO) {
                DocumentDetector.create(context).use { detector ->
                    detector.detectDocument(FileInput(page.originalImage)).quadrangle
                }
            }
            editFrameState.quadrangle = quadrangle
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            GeniusScanSDK.logger.error("Error detecting document: ${exception.message}")
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.adjust_borders)) }) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            EditFrameView(
                imageBitmap = imageBitmap,
                state = editFrameState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    onClick = {
                        Quadrangle.createFullQuadrangle().also { quadrangle ->
                            editFrameState.quadrangle = quadrangle
                            page.quadrangle = quadrangle
                        }
                    },
                ) {
                    Text(stringResource(R.string.full_image))
                }
                Button(onClick = {
                    page.quadrangle = editFrameState.quadrangle
                    onConfirmed()
                }) {
                    Text(stringResource(R.string.validate))
                }
            }
        }
    }
}
