package com.geniusscansdk.demo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.geniusscansdk.core.GeniusScanSDK
import com.geniusscansdk.demo.camera.ScanScreen
import com.geniusscansdk.demo.enhance.EnhancementScreen
import com.geniusscansdk.demo.enhance.PdfGenerator
import com.geniusscansdk.demo.model.DocumentPageState
import com.geniusscansdk.demo.model.Page
import com.geniusscansdk.demo.processing.EditFrameScreen
import com.geniusscansdk.demo.utils.restartApp
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (BuildConfig.GSSDK_COMPOSE_LICENSE_KEY.isNotEmpty()) {
            GeniusScanSDK.setLicenseKey(this, BuildConfig.GSSDK_COMPOSE_LICENSE_KEY, true)
        }
        setContent { MaterialTheme { DemoApp() } }
    }

    @Composable
    private fun DemoApp() {
        val navController = rememberNavController()
        val documentState = remember { DocumentPageState.getInstance(this) }
        val coroutineScope = rememberCoroutineScope()
        var isGeneratingPdf by remember { mutableStateOf(false) }
        var pdfGenerationProgress by remember { mutableStateOf(0) }

        NavHost(navController = navController, startDestination = Home) {
            composable<Home> {
                HomeScreen(
                    documentState = documentState,
                    onAddPage = { navController.navigate(Scan) },
                    isGeneratingPdf = isGeneratingPdf,
                    pdfGenerationProgress = pdfGenerationProgress,
                    onShare = { isOcrEnabled ->
                        coroutineScope.launch {
                            isGeneratingPdf = true
                            pdfGenerationProgress = 0
                            exportDocument(documentState.pages, isOcrEnabled) { progress ->
                                runOnUiThread { pdfGenerationProgress = progress }
                            }
                            isGeneratingPdf = false
                        }
                    },
                )
            }
            composable<Scan> {
                ScanScreen(
                    onPageCaptured = { page ->
                        documentState.currentPage = page
                        navController.navigate(BorderDetection)
                    },
                    onError = { this@MainActivity.restartApp() },
                )
            }
            composable<BorderDetection> {
                val page = documentState.currentPage
                if (page != null) {
                    EditFrameScreen(
                        page = page,
                        onConfirmed = { navController.navigate(Enhancement) },
                    )
                } else {
                    ReturnHome(navController)
                }
            }
            composable<Enhancement> {
                val page = documentState.currentPage
                if (page != null) {
                    EnhancementScreen(
                        page = page,
                        onPageSaved = { page ->
                            documentState.addPage(page)
                            documentState.currentPage = null
                            navController.popBackStack(Home, inclusive = false)
                        },
                    )
                } else {
                    ReturnHome(navController)
                }
            }
        }
    }

    @Composable
    private fun ReturnHome(navController: NavHostController) {
        LaunchedEffect(Unit) {
            navController.popBackStack(Home, inclusive = false)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun HomeScreen(
        documentState: DocumentPageState,
        onAddPage: () -> Unit,
        isGeneratingPdf: Boolean,
        pdfGenerationProgress: Int,
        onShare: (Boolean) -> Unit,
    ) {
        var isOcrEnabled by remember { mutableStateOf(false) }
        val pageCount = documentState.pages.size

        Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.gs_sdk_compose_demo_name)) }) }) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(pluralStringResource(R.plurals.page_count, pageCount, pageCount))
                Button(onClick = onAddPage) { Text(stringResource(R.string.open_camera)) }
                Text(stringResource(R.string.ocr))
                Switch(
                    checked = isOcrEnabled,
                    onCheckedChange = { isOcrEnabled = it },
                    enabled = pageCount > 0,
                )
                Button(
                    onClick = { onShare(isOcrEnabled) },
                    enabled = pageCount > 0 && !isGeneratingPdf,
                ) {
                    if (isGeneratingPdf) {
                        CircularProgressIndicator(progress = { pdfGenerationProgress / 100f })
                    } else {
                        Text(stringResource(R.string.generate_pdf))
                    }
                }
            }
        }
    }

    private suspend fun exportDocument(
        pages: List<Page>,
        isOcrEnabled: Boolean,
        onProgress: (Int) -> Unit,
    ) {
        val outputFile = File(externalCacheDir, "test.pdf")
        val pdfGenerator = PdfGenerator(this, pages, outputFile, isOcrEnabled, onProgress)
        try {
            pdfGenerator.generate()
            val uri = FileProvider.getUriForFile(
                this,
                "${BuildConfig.APPLICATION_ID}.fileprovider",
                outputFile,
            )
            startActivity(
                Intent(Intent.ACTION_VIEW, uri).apply {
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                }
            )
        } catch (exception: Exception) {
            Log.e(TAG, "Error generating PDF", exception)
            Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()
        }
    }

    @Serializable
    private data object Home

    @Serializable
    private data object Scan

    @Serializable
    private data object BorderDetection

    @Serializable
    private data object Enhancement

    private companion object {
        const val TAG: String = "MainActivity"
    }
}
