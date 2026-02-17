package com.geniusscansdk.simpledemo

import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.geniusscansdk.barcodeflow.BarcodeScanActivity
import com.geniusscansdk.barcodeflow.BarcodeScanFlowConfiguration
import com.geniusscansdk.barcodeflow.BarcodeScanFlowResult
import com.geniusscansdk.scanflow.FlowOutput
import com.geniusscansdk.scanflow.ScanFlowError
import com.geniusscansdk.scanflow.ScanFlowErrorCode
import com.geniusscansdk.simpledemo.ui.ConfigurationBooleanItem
import com.geniusscansdk.simpledemo.ui.ConfigurationColorItem
import com.geniusscansdk.simpledemo.ui.ConfigurationMultiChoiceItem
import com.geniusscansdk.simpledemo.ui.theme.SimpleDemoTheme
import com.geniusscansdk.simpledemo.ui.theme.sectionTitleStyle
import com.geniusscansdk.structureddata.Barcode
import com.geniusscansdk.structureddata.Barcode.Type.Code128
import com.geniusscansdk.structureddata.Barcode.Type.DataMatrix
import com.geniusscansdk.structureddata.Barcode.Type.QR
import java.util.EnumSet

class BarcodeActivity : AppCompatActivity() {

    private val viewModel: BarcodeViewModel by viewModels()
    private lateinit var barcodeFlowLauncher: ActivityResultLauncher<BarcodeScanFlowConfiguration>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        barcodeFlowLauncher = registerForActivityResult(BarcodeScanActivity.Contract()) { result ->
            viewModel.setScanResult(result)
        }

        setContentView(ComposeView(this).apply {
            setContent {
                SimpleDemoTheme {
                    val uiState by viewModel.uiState.collectAsState()
                    BarcodeScreen(
                        uiState = uiState,
                        onToggleBatchMode = viewModel::toggleBatchMode,
                        onCodeTypesSelected = viewModel::onCodeTypesSelected,
                        onHighlightColorSelected = viewModel::setHighlightColor,
                        onMenuColorSelected = viewModel::setMenuColor,
                        onScanClick = { configuration ->
                            barcodeFlowLauncher.launch(configuration)
                        },
                        clearResult = { viewModel.clearScanResult() },
                        finish = { finish() }
                    )
                }
            }
        })
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BarcodeScreen(
    uiState: BarcodeUiState,
    onToggleBatchMode: () -> Unit,
    onCodeTypesSelected: (List<Barcode.Type>) -> Unit,
    onHighlightColorSelected: (Int) -> Unit,
    onMenuColorSelected: (Int) -> Unit,
    onScanClick: (BarcodeScanFlowConfiguration) -> Unit,
    clearResult: () -> Unit,
    finish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val onBackClick = if (uiState.flowOutput == null) {
        finish
    } else {
        clearResult
    }
    BackHandler(onBack = onBackClick)

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.barcode_scanning)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = Icons.AutoMirrored.Filled.ArrowBack.name
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (uiState.flowOutput == null && uiState.selectedBarcodeTypes.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    text = { Text(stringResource(R.string.barcode_scan)) },
                    icon = { Icon(Icons.Default.QrCodeScanner, "Scan") },
                    onClick = {
                        val configuration = BarcodeScanFlowConfiguration(
                            isBatchModeEnabled = uiState.isBatchModeEnabled,
                            supportedCodeTypes = uiState.selectedBarcodeTypes,
                            highlightColor = uiState.highlightColor,
                            menuColor = uiState.menuColor
                        )
                        onScanClick(configuration)
                    },
                )
            }
        }
    ) { innerPadding ->
        if (uiState.flowOutput is FlowOutput.Success) {
            ResultsContent(
                codes = uiState.flowOutput.result.codes,
                modifier = Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
                    .fillMaxSize()
            )
        } else {
            ConfigurationContent(
                uiState = uiState,
                onToggleBatchMode = onToggleBatchMode,
                onCodeTypesSelected = onCodeTypesSelected,
                onHighlightColorSelected = onHighlightColorSelected,
                onMenuColorSelected = onMenuColorSelected,
                modifier = Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
                    .fillMaxSize()
            )

            uiState.flowOutput
                .let { it as? FlowOutput.Error }
                ?.error
                ?.takeUnless { it.code == ScanFlowErrorCode.CANCELLATION }
                ?.let { error ->
                    ErrorDialog(
                        error = error,
                        onDismissed = clearResult
                    )
                }
        }
    }
}

@Composable
private fun ConfigurationContent(
    uiState: BarcodeUiState,
    onToggleBatchMode: () -> Unit,
    onCodeTypesSelected: (List<Barcode.Type>) -> Unit,
    onHighlightColorSelected: (Int) -> Unit,
    onMenuColorSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {

        ConfigurationBooleanItem(
            label = stringResource(R.string.barcode_batch_mode),
            subTitle = if (uiState.isBatchModeEnabled) {
                stringResource(R.string.barcode_batch_mode_description)
            } else {
                stringResource(R.string.barcode_simple_mode_description)
            },
            checked = uiState.isBatchModeEnabled,
            onCheckChanged = { onToggleBatchMode() }
        )

        ConfigurationMultiChoiceItem(
            label = stringResource(R.string.barcode_supported_types),
            options = Barcode.Type.entries,
            selectedOptions = uiState.selectedBarcodeTypes.toList(),
            formatOption = { it.displayName },
            saveSelectedOptions = onCodeTypesSelected
        )

        HorizontalDivider(modifier = Modifier.padding(bottom = 24.dp))

        Text(
            stringResource(R.string.custom_scanning_ui),
            style = sectionTitleStyle(),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        ConfigurationColorItem(
            label = stringResource(R.string.custom_scanning_highlight_color),
            defaultColor = Color(uiState.highlightColor),
            saveColor = { color -> onHighlightColorSelected(color.toArgb()) }
        )

        ConfigurationColorItem(
            label = stringResource(R.string.custom_scanning_menu_color),
            defaultColor = Color(uiState.menuColor),
            saveColor = { color -> onMenuColorSelected(color.toArgb()) }
        )
    }
}

@Composable
private fun ResultsContent(
    codes: List<Barcode>,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier) {
        item {
            Text(
                stringResource(R.string.barcode_result_found),
                style = sectionTitleStyle(),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        items(codes) { code ->
            ListItem(
                headlineContent = { Text(code.type.displayName) },
                supportingContent = { Text(code.value) }
            )
        }
    }
}

@Composable
private fun ErrorDialog(
    error: ScanFlowError,
    onDismissed: () -> Unit
) {
    AlertDialog(
        title = { Text(stringResource(R.string.barcode_result_error)) },
        text = {
            Text(error.message)
        },
        onDismissRequest = onDismissed,
        confirmButton = {
            TextButton(onClick = onDismissed) {
                Text(stringResource(android.R.string.ok))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun BarcodeConfigurationPreview() {
    SimpleDemoTheme {
        ConfigurationContent(
            uiState = BarcodeUiState(
                isBatchModeEnabled = false,
                selectedBarcodeTypes = EnumSet.of(QR, Code128, DataMatrix),
            ),
            onToggleBatchMode = {},
            onCodeTypesSelected = {},
            onHighlightColorSelected = {},
            onMenuColorSelected = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BarcodeConfigurationBatchModePreview() {
    SimpleDemoTheme {
        ConfigurationContent(
            uiState = BarcodeUiState(
                isBatchModeEnabled = true,
                selectedBarcodeTypes = EnumSet.of(QR),
            ),
            onToggleBatchMode = {},
            onCodeTypesSelected = {},
            onHighlightColorSelected = {},
            onMenuColorSelected = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BarcodeResultsSuccessPreview() {
    SimpleDemoTheme {
        BarcodeScreen(
            uiState = BarcodeUiState(
                isBatchModeEnabled = true,
                selectedBarcodeTypes = EnumSet.of(
                    QR,
                    Barcode.Type.EAN13,
                    Code128
                ),
                flowOutput = FlowOutput.Success(BarcodeScanFlowResult(
                    listOf(
                        Barcode("123456789012", Barcode.Type.EAN13),
                        Barcode("ABCD1234", Code128),
                        Barcode("https://example.com", QR)
                    )
                ))
            ),
            onToggleBatchMode = {},
            onCodeTypesSelected = {},
            onHighlightColorSelected = {},
            onMenuColorSelected = {},
            onScanClick = {},
            clearResult = {},
            finish = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BarcodeResultsErrorPreview() {
    SimpleDemoTheme {
        BarcodeScreen(
            uiState = BarcodeUiState(
                isBatchModeEnabled = false,
                selectedBarcodeTypes = EnumSet.of(QR),
                flowOutput = FlowOutput.Error(ScanFlowError.capture("Camera permission is required"))
            ),
            onToggleBatchMode = {},
            onCodeTypesSelected = {},
            onHighlightColorSelected = {},
            onMenuColorSelected = {},
            onScanClick = {},
            clearResult = {},
            finish = {},
        )
    }
}
