package com.geniusscansdk.simpledemo

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.outlined.DocumentScanner
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.geniusscansdk.core.GeniusScanSDK
import com.geniusscansdk.scanflow.ScanConfiguration
import com.geniusscansdk.scanflow.ScanFlow
import com.geniusscansdk.simpledemo.helpers.ScanHelper
import com.geniusscansdk.simpledemo.helpers.ScanHelper.createBaseOcrConfiguration
import com.geniusscansdk.simpledemo.ui.theme.SimpleDemoTheme

class MainActivity: AppCompatActivity() {

    private lateinit var scanLauncher : ActivityResultLauncher<Intent>

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()

        // This code shows how to initialize the SDK with a license key.
        // Without a license key, the SDK runs for 60 seconds and then the app needs to be restarted.
        if (BuildConfig.GSSDK_LICENSE_KEY.isNotEmpty()) {
            GeniusScanSDK.setLicenseKey(this, BuildConfig.GSSDK_LICENSE_KEY, autoRefresh = true)
        }

        setContentView(ComposeView(this).apply {
            setContent {
                SimpleDemoTheme {
                    HomeScreen(
                        defaultScanFlowClick = ::scanFromCamera,
                        customScanFlowClick = {
                            startActivity(Intent(this@MainActivity, CustomScanFlowActivity::class.java))
                        },
                        barcodeFlowClick = {
                            startActivity(Intent(this@MainActivity, ReadableCodeActivity::class.java))
                        }
                    )
                }
            }
        })

        scanLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val intent = result.data.takeIf { result.resultCode == RESULT_OK }
            intent?.let {
                ScanHelper.getScanResult(intent, this@MainActivity)?.let { scanResult ->
                    val uri = FileProvider.getUriForFile(
                        this, BuildConfig.APPLICATION_ID + ".fileprovider",
                        scanResult.multiPageDocument!!
                    )
                    val resultIntent = Intent(Intent.ACTION_VIEW, uri)
                    resultIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    startActivity(resultIntent)
                }
            }
        }
    }

    private fun scanFromCamera() {
        val scanConfiguration = createBaseConfiguration()
        scanConfiguration.source = ScanConfiguration.Source.CAMERA
        startScanning(scanConfiguration)
    }

    private fun createBaseConfiguration(): ScanConfiguration {
        val scanConfiguration = ScanConfiguration()
        scanConfiguration.multiPage = true
        scanConfiguration.multiPageFormat = ScanConfiguration.MultiPageFormat.PDF
        scanConfiguration.pdfPageSize = ScanConfiguration.PdfPageSize.FIT
        scanConfiguration.pdfMaxScanDimension = 2000
        scanConfiguration.jpegQuality = 60
        scanConfiguration.postProcessingActions = ScanConfiguration.Action.ALL
        scanConfiguration.photoLibraryButtonHidden = false
        scanConfiguration.flashButtonHidden = false
        scanConfiguration.defaultFlashMode = ScanConfiguration.FlashMode.AUTO
        scanConfiguration.backgroundColor = ContextCompat.getColor(this, R.color.md_theme_background)
        scanConfiguration.foregroundColor = ContextCompat.getColor(this, R.color.md_theme_primary)
        scanConfiguration.highlightColor = Color.BLUE
        scanConfiguration.availableFilters = listOf(
            ScanConfiguration.Filter.NONE,
            ScanConfiguration.Filter.AUTOMATIC,
            ScanConfiguration.Filter.AUTOMATIC_BLACK_AND_WHITE,
            ScanConfiguration.Filter.AUTOMATIC_COLOR,
            ScanConfiguration.Filter.PHOTO,
            ScanConfiguration.Filter.SOFT_GRAYSCALE,
            ScanConfiguration.Filter.SOFT_COLOR,
            ScanConfiguration.Filter.STRONG_MONOCHROME
        )

        scanConfiguration.ocrConfiguration = createBaseOcrConfiguration()

        return scanConfiguration
    }

    private fun startScanning(scanConfiguration: ScanConfiguration) {
        val intent = ScanFlow.createScanFlowIntent(this@MainActivity, scanConfiguration)
        scanLauncher.launch(intent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    defaultScanFlowClick: () -> Unit,
    customScanFlowClick: () -> Unit,
    barcodeFlowClick: () -> Unit
) {
    val context = LocalContext.current
    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("GS SDK Simple Demo") }) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).consumeWindowInsets(innerPadding).fillMaxSize()) {
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                ListItem(
                    headlineContent = { Text(text = stringResource(R.string.custom_scanning_title)) },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Filled.DocumentScanner,
                            contentDescription = stringResource(R.string.default_scanflow)
                        )
                    },
                    supportingContent = { Text(text = stringResource(R.string.default_scanflow)) },
                    modifier = Modifier.clickable { defaultScanFlowClick() }
                )

                ListItem(
                    headlineContent = { Text(text = stringResource(R.string.custom_scanning_title)) },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Outlined.DocumentScanner,
                            contentDescription = stringResource(R.string.default_scanflow)
                        )
                    },
                    supportingContent = { Text(text = stringResource(R.string.custom_scanflow)) },
                    modifier = Modifier.clickable { customScanFlowClick() }
                )

                ListItem(
                    headlineContent = { Text(text = stringResource(R.string.structured_data_scanning)) },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Outlined.QrCodeScanner,
                            contentDescription = stringResource(R.string.default_scanflow)
                        )
                    },
                    modifier = Modifier.clickable {
                        context.startActivity(Intent(context, StructuredDataActivity::class.java))
                    }
                )

                ListItem(
                    headlineContent = { Text(text = stringResource(R.string.barcode_scanning)) },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Filled.QrCodeScanner,
                            contentDescription = stringResource(R.string.barcode_scanning_description)
                        )
                    },
                    supportingContent = { Text(text = stringResource(R.string.barcode_scanning_description)) },
                    modifier = Modifier.clickable { barcodeFlowClick() }
                )
            }

            Text(
                text = "v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 10.dp)
            )
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    SimpleDemoTheme {
        HomeScreen({}, {}, {})
    }
}
