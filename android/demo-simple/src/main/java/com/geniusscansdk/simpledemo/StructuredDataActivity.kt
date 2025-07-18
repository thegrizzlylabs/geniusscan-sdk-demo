package com.geniusscansdk.simpledemo

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RawRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.annotation.ExperimentalCoilApi
import coil3.asImage
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import coil3.compose.rememberAsyncImagePainter
import com.geniusscansdk.scanflow.ScanConfiguration
import com.geniusscansdk.scanflow.ScanFlow
import com.geniusscansdk.simpledemo.helpers.FileHelper
import com.geniusscansdk.simpledemo.helpers.ScanHelper
import com.geniusscansdk.simpledemo.ui.theme.SimpleDemoTheme
import com.geniusscansdk.structureddata.ReadableCode
import com.geniusscansdk.structureddata.ReceiptCategory
import com.geniusscansdk.structureddata.StructuredDataReceipt
import java.io.File
import java.util.Date
import java.util.EnumSet
import java.util.Locale

class StructuredDataActivity: AppCompatActivity() {

    private lateinit var scanLauncher : ActivityResultLauncher<Intent>

    private val viewModel = StructuredDataViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()

        scanLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val intent = result.data.takeIf { result.resultCode == RESULT_OK }
            intent?.let {
                ScanHelper.getScanResult(intent, this@StructuredDataActivity)?.let { scanResult ->
                    viewModel.showResult(scanResult)
                }
            }
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        setContentView(ComposeView(this).apply {
            setContent {
                SimpleDemoTheme {
                    StructuredDataScreen(
                        viewModel,
                        ::scan,
                        scanWithReceipt = {
                            scanWithImage(R.raw.receipt, "receipt.jpg", ScanConfiguration.StructuredData.RECEIPT)
                        },
                        scanWithQrCode = {
                            scanWithImage(R.raw.barcodes, "codes.jpg", ScanConfiguration.StructuredData.READABLE_CODE)
                        },
                        onBackClick = onBackPressedDispatcher::onBackPressed
                    )
                }
            }
        })
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (viewModel.uiState.value.pages.isEmpty()) {
                finish()
            } else {
                viewModel.clearPages()
            }
        }
    }

    private fun scan() {
        val scanConfiguration = ScanConfiguration().apply {
            source = ScanConfiguration.Source.CAMERA
            skipPostProcessingScreen = true
            structuredData = EnumSet.allOf(ScanConfiguration.StructuredData::class.java)
        }
        startScanFlow(scanConfiguration)
    }

    private fun scanWithImage(@RawRes image: Int, fileName: String, structureData: ScanConfiguration.StructuredData) {
        val scanConfiguration = ScanConfiguration().apply {
            source = ScanConfiguration.Source.IMAGE
            sourceImage = File(externalCacheDir, fileName).apply {
                FileHelper.copyFileFromResource(image, destinationFile = this, resources)
            }
            skipPostProcessingScreen = true
            structuredData = EnumSet.of(structureData)
        }
        startScanFlow(scanConfiguration)
    }

    private fun startScanFlow(scanConfiguration: ScanConfiguration) {
        viewModel.clearPages()

        val intent = ScanFlow.createScanFlowIntent(this@StructuredDataActivity, scanConfiguration)
        scanLauncher.launch(intent)
    }
}

@Composable
private fun StructuredDataScreen(
    viewModel: StructuredDataViewModel,
    scan: () -> Unit,
    scanWithReceipt: () -> Unit,
    scanWithQrCode: () -> Unit,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    StructuredDataScreen(
        uiState,
        scan,
        scanWithReceipt,
        scanWithQrCode,
        onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StructuredDataScreen(
    uiState: StructuredDataUiState,
    scan: () -> Unit,
    scanWithReceipt: () -> Unit,
    scanWithQrCode: () -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.structured_data_scanning)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = Icons.AutoMirrored.Filled.ArrowBack.name
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).consumeWindowInsets(innerPadding).fillMaxSize()) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                if (uiState.pages.isEmpty()) {
                    MenuScreen(scan, scanWithReceipt, scanWithQrCode)
                } else {
                    ResultScreen(uiState)
                }
            }
        }
    }
}

@Composable
private fun MenuScreen(
    scan: () -> Unit,
    scanWithReceipt: () -> Unit,
    scanWithQrCode: () -> Unit
) {
    ListItem(
        headlineContent = { Text(text = stringResource(R.string.structured_data_scanning_start_scanning)) },
        leadingContent = {
            Icon(
                imageVector = Icons.Default.PhotoCamera,
                contentDescription = stringResource(R.string.structured_data_scanning_start_scanning)
            )
        },
        modifier = Modifier.clickable { scan() }
    )

    ListItem(
        headlineContent = { Text(text = stringResource(R.string.structured_data_scanning_try_receipt)) },
        leadingContent = {
            Icon(
                imageVector = Icons.Default.Receipt,
                contentDescription = stringResource(R.string.structured_data_scanning_try_receipt)
            )
        },
        modifier = Modifier.clickable { scanWithReceipt() }
    )

    ListItem(
        headlineContent = { Text(text = stringResource(R.string.structured_data_scanning_try_qrcode)) },
        leadingContent = {
            Icon(
                imageVector = Icons.Default.QrCode2,
                contentDescription = stringResource(R.string.structured_data_scanning_try_qrcode)
            )
        },
        modifier = Modifier.clickable { scanWithQrCode() }
    )
}

@Composable
private fun ResultScreen(uiState: StructuredDataUiState) {
    val pagerState = rememberPagerState(pageCount = { uiState.pages.size })

    HorizontalPager(state = pagerState) {
        val page = uiState.pages[it]
        Column(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = rememberAsyncImagePainter(page.image),
                contentScale = ContentScale.FillHeight,
                contentDescription = null,
                modifier = Modifier.size(350.dp).align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(12.dp))

            page.receipt?.let { receipt ->
                HorizontalDivider(modifier = Modifier.padding(bottom = 24.dp))

                Text(
                    stringResource(R.string.structured_data_receipt),
                    style = sectionTitleStyle(),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                ListItem(
                    headlineContent = { Text(stringResource(R.string.structured_data_locale)) },
                    supportingContent = {
                        Text(receipt.locale?.displayName ?: stringResource(R.string.structured_data_not_found))
                    }
                )
                ListItem(
                    headlineContent = { Text(stringResource(R.string.structured_data_date)) },
                    supportingContent = {
                        Text(receipt.date?.toString() ?: stringResource(R.string.structured_data_not_found))
                    }
                )
                ListItem(
                    headlineContent = { Text(stringResource(R.string.structured_data_category)) },
                    supportingContent = {
                        Text(receipt.category?.name ?: stringResource(R.string.structured_data_not_found))
                    }
                )
                ListItem(
                    headlineContent = { Text(stringResource(R.string.structured_data_merchant)) },
                    supportingContent = {
                        Text(receipt.merchant ?: stringResource(R.string.structured_data_not_found))
                    }
                )
                ListItem(
                    headlineContent = { Text(stringResource(R.string.structured_data_currency)) },
                    supportingContent = {
                        Text(receipt.currency ?: stringResource(R.string.structured_data_not_found))
                    }
                )
                ListItem(
                    headlineContent = { Text(stringResource(R.string.structured_data_amount)) },
                    supportingContent = {
                        Text(receipt.amount?.toString() ?: stringResource(R.string.structured_data_not_found))
                    }
                )
            }

            if (!page.barcodes.isNullOrEmpty()) {
                HorizontalDivider(modifier = Modifier.padding(bottom = 24.dp))

                Text(
                    stringResource(R.string.structured_data_barcodes),
                    style = sectionTitleStyle(),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                page.barcodes.forEach { barcode ->
                    ListItem(
                        headlineContent = { Text(barcode.type.name) },
                        supportingContent = { Text(barcode.value) }
                    )
                }
            }
        }
    }

    if (pagerState.pageCount > 1) {
        Row(
            Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val color =
                    if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outlineVariant
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(8.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun StructuredDataScreenPreview() {
    SimpleDemoTheme {
        StructuredDataScreen(StructuredDataUiState(), {}, {}, {}, {})
    }
}

@SuppressLint("ResourceType")
@OptIn(ExperimentalCoilApi::class)
@Preview
@Composable
private fun StructuredDataScreenPreview_receipt() {
    SimpleDemoTheme {
        val context = LocalContext.current
        val previewHandler = AsyncImagePreviewHandler {
            AppCompatResources.getDrawable(context, R.raw.receipt)?.asImage()
        }
        CompositionLocalProvider(LocalAsyncImagePreviewHandler provides previewHandler) {
            StructuredDataScreen(
                StructuredDataUiState(pages = listOf(Page(
                    receipt = StructuredDataReceipt(
                        locale = Locale.FRANCE,
                        merchant = null,
                        amount = 123.3,
                        currency = "$",
                        date = Date(),
                        category = ReceiptCategory.RESTAURANT
                    )
                ))),
                {}, {}, {}, {}
            )
        }
    }
}

@SuppressLint("ResourceType")
@OptIn(ExperimentalCoilApi::class)
@Preview
@Composable
private fun StructuredDataScreenPreview_readableCodes() {
    SimpleDemoTheme {
        val context = LocalContext.current
        val previewHandler = AsyncImagePreviewHandler {
            AppCompatResources.getDrawable(context, R.raw.barcodes)?.asImage()
        }
        CompositionLocalProvider(LocalAsyncImagePreviewHandler provides previewHandler) {
            StructuredDataScreen(
                StructuredDataUiState(pages = listOf(Page(
                    barcodes = listOf(
                        ReadableCode(value = "12345", type = ReadableCode.Type.Code39),
                        ReadableCode(value = "Hello", type = ReadableCode.Type.QR),
                        ReadableCode(value = "test", type = ReadableCode.Type.DataMatrix)
                    )
                ))),
                {}, {}, {}, {}
            )
        }
    }
}
