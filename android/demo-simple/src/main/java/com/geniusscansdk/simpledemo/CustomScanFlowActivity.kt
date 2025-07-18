package com.geniusscansdk.simpledemo

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.DocumentScanner
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.geniusscansdk.core.ScanProcessor
import com.geniusscansdk.scanflow.ScanConfiguration
import com.geniusscansdk.scanflow.ScanFlow
import com.geniusscansdk.simpledemo.helpers.FileHelper
import com.geniusscansdk.simpledemo.helpers.ScanHelper
import com.geniusscansdk.simpledemo.ui.theme.SimpleDemoTheme
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import java.io.File
import java.util.EnumSet
import java.util.Locale

class CustomScanFlowActivity: AppCompatActivity() {

    private lateinit var scanLauncher : ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()

        setContentView(ComposeView(this).apply {
            setContent {
                SimpleDemoTheme {
                    CustomScreen(
                        onBackClick = onBackPressedDispatcher::onBackPressed,
                        startScanFlow = ::startScanning
                    )
                }
            }
        })

        scanLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val intent = result.data.takeIf { result.resultCode == RESULT_OK }
            intent?.let {
                ScanHelper.getScanResult(intent, this@CustomScanFlowActivity)?.let { scanResult ->
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

    private fun startScanning(scanConfiguration: ScanConfiguration) {
        if (scanConfiguration.source == ScanConfiguration.Source.IMAGE) {
            scanConfiguration.sourceImage = File(externalCacheDir, "temp.jpg").apply {
                FileHelper.copyFileFromResource(R.raw.scan, destinationFile = this, resources)
            }
        }

        val intent = ScanFlow.createScanFlowIntent(this@CustomScanFlowActivity, scanConfiguration)
        scanLauncher.launch(intent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomScreen(
    onBackClick: () -> Unit,
    startScanFlow: (ScanConfiguration) -> Unit
) {
    var scanConfiguration by rememberSaveable { mutableStateOf(ScanConfiguration()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.custom_scanning_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = Icons.AutoMirrored.Filled.ArrowBack.name
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { startScanFlow(scanConfiguration) },
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.DocumentScanner,
                        contentDescription = stringResource(R.string.scan)
                    )
                },
                text = {
                    Text(stringResource(R.string.scan))
                }
            )
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 68.dp) // save button
        ) {
            Text(
                text = stringResource(R.string.custom_scanning_description),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            ConfigurationListItem(
                label = stringResource(R.string.custom_scanning_source),
                selectedOption = scanConfiguration.source,
                options = ScanConfiguration.Source.entries,
                formatOption = { option -> option.name.capitalize() },
                onOptionSelected = { option -> scanConfiguration = scanConfiguration.copy(source = option) }
            )

            HorizontalDivider(modifier = Modifier.padding(bottom = 24.dp))

            CameraScreen(scanConfiguration) {
                scanConfiguration = it
            }

            HorizontalDivider(modifier = Modifier.padding(bottom = 24.dp))

            PostProcessingScreen(scanConfiguration) {
                scanConfiguration = it
            }

            HorizontalDivider(modifier = Modifier.padding(bottom = 24.dp))

            PostProcessingActions(scanConfiguration) {
                scanConfiguration = it
            }

            HorizontalDivider(modifier = Modifier.padding(bottom = 24.dp))

            Output(scanConfiguration) {
                scanConfiguration = it
            }

            HorizontalDivider(modifier = Modifier.padding(bottom = 24.dp))

            UI(scanConfiguration) {
                scanConfiguration = it
            }

            HorizontalDivider(modifier = Modifier.padding(bottom = 24.dp))

            OCR(scanConfiguration) {
                scanConfiguration = it
            }

        }
    }
}

@Composable
private fun CameraScreen(
    scanConfiguration: ScanConfiguration,
    onScanConfigurationModified: (ScanConfiguration) -> Unit
) {
    Text(
        stringResource(R.string.custom_scanning_camera_screen),
        style = sectionTitleStyle(),
        modifier = Modifier.padding(horizontal = 16.dp)
    )

    ConfigurationBooleanItem(
        label = stringResource(R.string.custom_scanning_display_photo_library_button),
        checked = !scanConfiguration.photoLibraryButtonHidden,
        onCheckChanged = { checked ->
            onScanConfigurationModified(scanConfiguration.copy(photoLibraryButtonHidden = !checked))
        }
    )

    ConfigurationBooleanItem(
        label = stringResource(R.string.custom_scanning_display_flash_button),
        checked = !scanConfiguration.flashButtonHidden,
        onCheckChanged = { checked ->
            onScanConfigurationModified(scanConfiguration.copy(flashButtonHidden = !checked))
        }
    )

    ConfigurationListItem(
        label = stringResource(R.string.custom_scanning_default_flash_mode),
        selectedOption = scanConfiguration.defaultFlashMode,
        options = ScanConfiguration.FlashMode.entries,
        formatOption = { option -> option.name.capitalize() },
        onOptionSelected = { option -> onScanConfigurationModified(scanConfiguration.copy(defaultFlashMode = option)) }
    )
}

@Composable
private fun PostProcessingScreen(
    scanConfiguration: ScanConfiguration,
    onScanConfigurationModified: (ScanConfiguration) -> Unit
) {
    val context = LocalContext.current
    Text(
        stringResource(R.string.custom_scanning_post_processing_screen),
        style = sectionTitleStyle(),
        modifier = Modifier.padding(horizontal = 16.dp)
    )

    ConfigurationListItem(
        label = stringResource(R.string.custom_scanning_default_scan_orientation),
        selectedOption = scanConfiguration.defaultScanOrientation,
        options = ScanConfiguration.Orientation.entries,
        formatOption = { option -> option.name.capitalize() },
        onOptionSelected = { option ->
            onScanConfigurationModified(scanConfiguration.copy(defaultScanOrientation = option))
        }
    )

    ConfigurationListItem(
        label = stringResource(R.string.custom_scanning_default_filter),
        selectedOption = scanConfiguration.defaultFilter,
        options = ScanConfiguration.Filter.entries,
        formatOption = { option -> context.getString(option.labelResId) },
        onOptionSelected = { option -> onScanConfigurationModified(scanConfiguration.copy(defaultFilter = option)) }
    )

    ConfigurationListItem(
        label = stringResource(R.string.custom_scanning_readability),
        selectedOption = scanConfiguration.requiredReadabilityLevel,
        options = ScanProcessor.ReadabilityLevel.entries,
        formatOption = { option -> option.name.capitalize() },
        onOptionSelected = { option ->
            onScanConfigurationModified(scanConfiguration.copy(requiredReadabilityLevel = option))
        }
    )

    ConfigurationBooleanItem(
        label = stringResource(R.string.custom_scanning_default_curvature_correction),
        checked = scanConfiguration.defaultCurvatureCorrection == ScanConfiguration.CurvatureCorrectionMode.ENABLED,
        onCheckChanged = { checked ->
            val curvature = if (checked) {
                ScanConfiguration.CurvatureCorrectionMode.ENABLED
            } else {
                ScanConfiguration.CurvatureCorrectionMode.DISABLED
            }
            onScanConfigurationModified(scanConfiguration.copy(defaultCurvatureCorrection = curvature))
        }
    )
}

@Composable
private fun PostProcessingActions(
    scanConfiguration: ScanConfiguration,
    onScanConfigurationModified: (ScanConfiguration) -> Unit
) {
    Text(
        stringResource(R.string.custom_scanning_post_processing_actions),
        style = sectionTitleStyle(),
        modifier = Modifier.padding(horizontal = 16.dp)
    )

    ConfigurationMultiChoiceItem(
        label = stringResource(R.string.custom_scanning_available_actions),
        selectedOptions = scanConfiguration.postProcessingActions.toList(),
        options = ScanConfiguration.Action.ALL.toList(),
        formatOption = { option -> option.name.capitalize().replace(oldValue = "_", newValue = " ") },
        saveSelectedOptions = { actions ->
            onScanConfigurationModified(scanConfiguration.copy(
                postProcessingActions = actions.toCollection(EnumSet.noneOf(ScanConfiguration.Action::class.java))
            ))
        }
    )

    if (scanConfiguration.postProcessingActions.contains(ScanConfiguration.Action.EDIT_FILTER)) {
        ConfigurationMultiChoiceItem(
            label = stringResource(R.string.custom_scanning_available_filters),
            selectedOptions = scanConfiguration.availableFilters,
            options = ScanConfiguration.Filter.entries,
            formatOption = { option -> option.name.capitalize().replace(oldValue = "_", newValue = " ") },
            saveSelectedOptions = { filters ->
                onScanConfigurationModified(scanConfiguration.copy(
                    availableFilters = filters.takeIf { it.isNotEmpty() } ?: listOf(ScanConfiguration.Filter.NONE)
                ))
            }
        )
    }
}

@Composable
private fun Output(
    scanConfiguration: ScanConfiguration,
    onScanConfigurationModified: (ScanConfiguration) -> Unit
) {
    Text(
        stringResource(R.string.custom_scanning_output),
        style = sectionTitleStyle(),
        modifier = Modifier.padding(horizontal = 16.dp)
    )

    ConfigurationBooleanItem(
        label = stringResource(R.string.custom_scanning_multipage),
        checked = scanConfiguration.multiPage,
        onCheckChanged = { checked ->
            onScanConfigurationModified(scanConfiguration.copy(multiPage = checked))
        }
    )

    ConfigurationListItem(
        label = stringResource(R.string.custom_scanning_format),
        selectedOption = scanConfiguration.multiPageFormat,
        options = ScanConfiguration.MultiPageFormat.entries,
        formatOption = { option -> option.name.capitalize() },
        onOptionSelected = { option -> onScanConfigurationModified(scanConfiguration.copy(multiPageFormat = option)) }
    )

    ConfigurationBooleanItem(
        label = stringResource(R.string.custom_scanning_resize_scans),
        checked = scanConfiguration.pdfMaxScanDimension != 0,
        onCheckChanged = { checked ->
            onScanConfigurationModified(scanConfiguration.copy(
                pdfMaxScanDimension = if (checked) 1000 else 0
            ))
        }
    )

    if (scanConfiguration.pdfMaxScanDimension != 0) {
        ListItem(
            headlineContent = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = stringResource(R.string.custom_scanning_max_size))
                    CustomSlider(
                        value = scanConfiguration.pdfMaxScanDimension.toFloat() / 10000,
                        onValueChange = { position ->
                            onScanConfigurationModified(scanConfiguration.copy(pdfMaxScanDimension = (position * 10000).toInt()))
                        },
                        modifier = Modifier.padding(horizontal = 4.dp).weight(1f)
                    )
                    Text(text = "${scanConfiguration.pdfMaxScanDimension}", maxLines = 1)
                }
            }
        )
    }

    ListItem(
        headlineContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = stringResource(R.string.custom_scanning_jpeg_quality))
                CustomSlider(
                    value = scanConfiguration.jpegQuality.toFloat() / 100,
                    onValueChange = { position ->
                        onScanConfigurationModified(scanConfiguration.copy(jpegQuality = (position * 100).toInt()))
                    },
                    modifier = Modifier.padding(horizontal = 4.dp).weight(1f)
                )
                Text(text = "${scanConfiguration.jpegQuality}", maxLines = 1)
            }
        }
    )

    ConfigurationListItem(
        label = stringResource(R.string.custom_scanning_page_size),
        selectedOption = scanConfiguration.pdfPageSize,
        options = ScanConfiguration.PdfPageSize.entries,
        formatOption = { option -> option.name.capitalize() },
        onOptionSelected = { option -> onScanConfigurationModified(scanConfiguration.copy(pdfPageSize = option)) }
    )
}

@Composable
private fun UI(
    scanConfiguration: ScanConfiguration,
    onScanConfigurationModified: (ScanConfiguration) -> Unit
) {
    Text(
        stringResource(R.string.custom_scanning_ui),
        style = sectionTitleStyle(),
        modifier = Modifier.padding(horizontal = 16.dp)
    )

    ConfigurationColorItem(
        label = stringResource(R.string.custom_scanning_foreground_color),
        defaultColor = Color(scanConfiguration.foregroundColor),
        saveColor = { color ->
            onScanConfigurationModified(scanConfiguration.copy(foregroundColor = color.toArgb()))
        }
    )

    ConfigurationColorItem(
        label = stringResource(R.string.custom_scanning_background_color),
        defaultColor = Color(scanConfiguration.backgroundColor),
        saveColor = { color ->
            onScanConfigurationModified(scanConfiguration.copy(backgroundColor = color.toArgb()))
        }
    )

    ConfigurationColorItem(
        label = stringResource(R.string.custom_scanning_highlight_color),
        defaultColor = Color(scanConfiguration.highlightColor),
        saveColor = { color ->
            onScanConfigurationModified(scanConfiguration.copy(highlightColor = color.toArgb()))
        }
    )
}

@Composable
private fun OCR(
    scanConfiguration: ScanConfiguration,
    onScanConfigurationModified: (ScanConfiguration) -> Unit
) {
    Text(
        stringResource(R.string.custom_scanning_ocr),
        style = sectionTitleStyle(),
        modifier = Modifier.padding(horizontal = 16.dp)
    )

    ConfigurationBooleanItem(
        label = stringResource(R.string.custom_scanning_ocr),
        checked = scanConfiguration.ocrConfiguration != null,
        onCheckChanged = { checked ->
            onScanConfigurationModified(scanConfiguration.copy(
                ocrConfiguration = if (checked) ScanHelper.createBaseOcrConfiguration() else null)
            )
        }
    )
}

@Composable
fun sectionTitleStyle() = TextStyle(
    color = MaterialTheme.colorScheme.primary,
    fontWeight = FontWeight.Medium,
    fontSize = 16.sp
)

@Composable
private fun ConfigurationBooleanItem(
    label: String,
    checked: Boolean,
    onCheckChanged: (Boolean) -> Unit
) {
    ListItem(
        headlineContent = { Text(text = label) },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckChanged
            )
        }
    )
}

@OptIn(ExperimentalStdlibApi::class)
@Composable
private fun ConfigurationColorItem(
    label: String,
    defaultColor: Color,
    saveColor: (Color) -> Unit
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var color by remember { mutableStateOf(defaultColor) }

    ListItem(
        headlineContent = { Text(text = label) },
        trailingContent = {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(defaultColor, shape = CircleShape)
                    .border(
                        width = 2.dp,
                        brush = Brush.linearGradient(listOf(Color.Yellow, Color.Red, Color.Blue, Color.Green)),
                        shape = CircleShape
                    )
                    .padding(2.dp)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.surface,
                        shape = CircleShape
                    )
                    .padding(2.dp)
            )
        },
        modifier = Modifier.clickable { showDialog = true }
    )

    if (showDialog) {
        val controller = rememberColorPickerController()

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(label) },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    HsvColorPicker(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(350.dp)
                            .padding(10.dp),
                        controller = controller,
                        initialColor = defaultColor,
                        onColorChanged = { colorEnvelope ->
                            color = colorEnvelope.color
                        }
                    )

                    BrightnessSlider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .height(35.dp),
                        controller = controller,
                        initialColor = defaultColor
                    )

                    AlphaSlider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .height(35.dp),
                        controller = controller,
                        initialColor = defaultColor
                    )

                    Text(text = "#${color.toArgb().toHexString()}")

                    AlphaTile(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(MaterialTheme.shapes.small),
                        controller = controller
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    saveColor(color)
                    showDialog = false
                }) {
                    Text(text = stringResource(id = android.R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(id = android.R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun <E> ConfigurationListItem(
    label: String,
    selectedOption: E,
    options: List<E>,
    formatOption: (E) -> String,
    onOptionSelected: (E) -> Unit
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }

    ListItem(
        headlineContent = { Text(text = label) },
        supportingContent = { Text(text = formatOption(selectedOption)) },
        modifier = Modifier.clickable { showDialog = true }
    )
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(label) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    options.forEach { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showDialog = false
                                    onOptionSelected(option)
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedOption == option,
                                onClick = {
                                    showDialog = false
                                    onOptionSelected(option)
                                }
                            )

                            Text(text = formatOption(option))
                        }

                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(id = android.R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun <E> ConfigurationMultiChoiceItem(
    label: String,
    selectedOptions: List<E>,
    options: List<E>,
    formatOption: (E) -> String,
    saveSelectedOptions: (List<E>) -> Unit
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }
    val localSelectedOptions = remember(selectedOptions) {
        mutableStateListOf<E>().apply { addAll(selectedOptions) }
    }

    ListItem(
        headlineContent = { Text(text = label) },
        supportingContent = {
            val displayedOptions = if (selectedOptions.isEmpty()) {
                stringResource(R.string.none)
            } else {
                selectedOptions.joinToString(", ") { formatOption(it) }
            }
            Text(displayedOptions)
        },
        modifier = Modifier.clickable { showDialog = true }
    )

    if (showDialog) {
        val onOptionChecked = { option: E ->
            val checked = localSelectedOptions.contains(option)
            if (checked) {
                localSelectedOptions.remove(option)
            } else {
                localSelectedOptions.add(option)
            }
        }
        val dismissDialog = {
            showDialog = false
            localSelectedOptions.clear()
            localSelectedOptions.addAll(selectedOptions)
        }
        AlertDialog(
            onDismissRequest = { dismissDialog() },
            title = { Text(label) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    options.forEach { option: E ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOptionChecked(option) },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = localSelectedOptions.contains(option),
                                onCheckedChange = { onOptionChecked(option) }
                            )

                            Text(text = formatOption(option))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    saveSelectedOptions(localSelectedOptions)
                }) {
                    Text(stringResource(id = android.R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { dismissDialog() }) {
                    Text(stringResource(id = android.R.string.cancel))
                }
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomSlider(
    modifier: Modifier = Modifier,
    value: Float,
    onValueChange: (Float) -> Unit,
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        track = { positions ->
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Box(
                    Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(positions.value)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        },
        thumb = {
            Box(modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
            )
        },
        modifier = modifier
    )
}

private fun String.capitalize(): String {
    return this.lowercase().replaceFirstChar { it.titlecase(Locale.getDefault()) }
}

@Preview
@Composable
private fun CustomScreenPreview() {
    SimpleDemoTheme {
        CustomScreen({}, {})
    }
}
