package com.geniusscansdk.simpledemo.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.geniusscansdk.simpledemo.R
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

@Composable
fun ConfigurationBooleanItem(
    label: String,
    subTitle: String? = null,
    checked: Boolean,
    onCheckChanged: (Boolean) -> Unit
) {
    ListItem(
        headlineContent = { Text(text = label) },
        supportingContent = { subTitle?.let { Text(text = it) } },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckChanged
            )
        }
    )
}

@Composable
fun <E> ConfigurationMultiChoiceItem(
    label: String,
    options: List<E>,
    selectedOptions: List<E>,
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

@OptIn(ExperimentalStdlibApi::class)
@Composable
fun ConfigurationColorItem(
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
