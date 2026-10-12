package com.geniusscansdk.demo.camera

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.geniusscansdk.camera.compose.DocumentScanScreen
import com.geniusscansdk.camera.compose.rememberScanScreenState
import com.geniusscansdk.camera.compose.takePicture
import com.geniusscansdk.core.GeniusScanSDK
import com.geniusscansdk.core.QuadStreamAnalyzer
import com.geniusscansdk.core.RotationAngle
import com.geniusscansdk.demo.R
import com.geniusscansdk.demo.model.Page
import com.geniusscansdk.util.ExperimentalApi
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

@Composable
fun ScanScreen(
    onPageCaptured: (Page) -> Unit,
    onError: () -> Unit,
) {
    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasCameraPermission = granted }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    if (hasCameraPermission) {
        CameraContent(onPageCaptured, onError)
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                Text(stringResource(R.string.allow_camera_access))
            }
        }
    }
}

@OptIn(ExperimentalApi::class)
@Composable
private fun CameraContent(
    onPageCaptured: (Page) -> Unit,
    onError: () -> Unit,
) {
    val context = LocalContext.current
    val scanScreenState = rememberScanScreenState()
    val coroutineScope = rememberCoroutineScope()
    var cameraReady by remember { mutableStateOf(false) }
    var captureInProgress by remember { mutableStateOf(false) }
    var guidanceResId by remember { mutableStateOf<Int?>(R.string.looking_for_document) }
    var cameraError by remember { mutableStateOf<String?>(null) }
    val cameraErrorMessage = stringResource(R.string.camera_error)
    val captureFailedMessage = stringResource(R.string.capture_failed)

    fun takePicture() {
        if (!cameraReady || captureInProgress) return
        captureInProgress = true
        coroutineScope.launch {
            val outputFile = File(
                checkNotNull(context.getExternalFilesDir(null)),
                "${UUID.randomUUID()}.jpeg",
            )
            runCatching {
                scanScreenState.takePicture(outputFile)
                GeniusScanSDK.rotateImage(outputFile.absolutePath, outputFile.absolutePath, RotationAngle.ROTATION_0)
            }
                .onSuccess { onPageCaptured(Page(outputFile)) }
                .onFailure { exception ->
                    Log.e(TAG, "Capture failed", exception)
                    Toast.makeText(context, captureFailedMessage, Toast.LENGTH_SHORT).show()
                    captureInProgress = false
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .safeDrawingPadding()
            .padding(vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DocumentScanScreen(
            modifier = Modifier
                .wrapContentSize()
                .weight(1f),
            state = scanScreenState,
            isDetectionEnabled = !captureInProgress,
            shutterAnimationEnabled = true,
            autoTriggerAnimationEnabled = true,
            onBorderDetectionResult = { result ->
                guidanceResId = when {
                    result.status == QuadStreamAnalyzer.Status.TRIGGER -> {
                        takePicture()
                        null
                    }
                    result.status == QuadStreamAnalyzer.Status.NOT_FOUND ||
                            result.resultQuadrangle == null -> R.string.looking_for_document
                    else -> R.string.document_found_please_remain_steady
                }
            },
            onBorderDetectionFailure = { cameraError = it.message },
            onCameraReady = { cameraReady = true },
            onCameraFailure = {
                Log.e(TAG, "Camera error", it)
                cameraError = it.message ?: cameraErrorMessage
            },
        )
        guidanceResId?.let { Text(stringResource(it), color = Color.White) }
        Button(
            onClick = ::takePicture,
            enabled = cameraReady && !captureInProgress,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(R.string.capture),
                    modifier = Modifier.alpha(if (captureInProgress) 0f else 1f),
                )
                if (captureInProgress) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                    )
                }
            }
        }
    }

    cameraError?.let { message ->
        AlertDialog(
            onDismissRequest = {},
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = onError) { Text(stringResource(R.string.restart)) }
            },
        )
    }
}

private const val TAG = "ScanScreen"
