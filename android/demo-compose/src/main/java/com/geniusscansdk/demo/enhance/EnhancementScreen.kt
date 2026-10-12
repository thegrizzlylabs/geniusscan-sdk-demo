package com.geniusscansdk.demo.enhance

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.geniusscansdk.core.RotationAngle
import com.geniusscansdk.demo.R
import com.geniusscansdk.demo.model.Page
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancementScreen(
    page: Page,
    onPageSaved: (Page) -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val pageProcessor = remember { PageProcessing(context) }
    var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    fun processPage(rotationAngle: RotationAngle? = null) {
        if (isProcessing) return
        isProcessing = true
        coroutineScope.launch {
            runCatching {
                pageProcessor.process(page, rotationAngle)
            }.onSuccess { bitmap = it }
                .onFailure { error = it.message }
            isProcessing = false
        }
    }

    LaunchedEffect(page) {
        bitmap = withContext(Dispatchers.IO) {
            BitmapFactory.decodeFile(page.originalImage.absolutePath).asImageBitmap()
        }
        processPage()
    }

    DisposableEffect(pageProcessor) {
        onDispose { pageProcessor.close() }
    }

    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.enhance_scan)) }) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.Black),
                contentAlignment = Alignment.Center,
            ) {
                bitmap?.let {
                    Image(
                        bitmap = it,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit,
                    )
                }
                if (isProcessing) CircularProgressIndicator()
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(onClick = { showFilterDialog = true }, enabled = !isProcessing) {
                    Text(stringResource(R.string.filter))
                }
                Button(
                    onClick = {
                        page.isDistortionCorrectionEnabled =
                            !page.isDistortionCorrectionEnabled
                        coroutineScope.launch {
                            processPage()
                        }
                    },
                    enabled = !isProcessing,
                ) {
                    Text(
                        if (page.isDistortionCorrectionEnabled) {
                            stringResource(R.string.flattened)
                        } else stringResource(
                            R.string.curved
                        )
                    )
                }
                Button(
                    onClick = {
                        coroutineScope.launch {
                            processPage(rotationAngle = RotationAngle.ROTATION_90_CCW)
                        }
                    },
                    enabled = !isProcessing,
                ) {
                    Text(stringResource(R.string.rotate))
                }
                Button(
                    onClick = { onPageSaved(page) },
                    enabled = !isProcessing,
                ) {
                    Text(stringResource(R.string.validate))
                }
            }
        }
    }

    if (showFilterDialog) {
        AlertDialog(
            onDismissRequest = { showFilterDialog = false },
            title = { Text(stringResource(R.string.choose_enhancement)) },
            text = {
                Column {
                    Filter.entries.forEach { filter ->
                        val selected = filter == (page.filter ?: Filter.AUTO)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = selected,
                                    role = Role.RadioButton,
                                    onClick = {
                                        page.filter = filter
                                        showFilterDialog = false
                                        processPage()
                                    },
                                )
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = selected,
                                onClick = null,
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.colorScheme.primary,
                                    unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                ),
                            )
                            Text(
                                text = stringResource(filter.displayId),
                                modifier = Modifier.padding(start = 8.dp),
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
            },
            confirmButton = {},
        )
    }

    error?.let { message ->
        AlertDialog(
            onDismissRequest = { error = null },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = { error = null }) { Text(stringResource(R.string.ok)) }
            },
        )
    }
}
