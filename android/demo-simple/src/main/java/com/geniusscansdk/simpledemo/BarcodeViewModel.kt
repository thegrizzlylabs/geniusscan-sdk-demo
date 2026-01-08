package com.geniusscansdk.simpledemo

import android.app.Application
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import com.geniusscansdk.barcodeflow.BarcodeFlowResult
import com.geniusscansdk.structureddata.Barcode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.EnumSet

class BarcodeViewModel(application: Application) : AndroidViewModel(application) {
    
    private val _uiState = MutableStateFlow(BarcodeUiState(menuColor = ContextCompat.getColor(application, R.color.md_theme_primary)))
    val uiState: StateFlow<BarcodeUiState> = _uiState.asStateFlow()
    
    fun toggleBatchMode() {
        _uiState.value = _uiState.value.copy(isBatchModeEnabled = !_uiState.value.isBatchModeEnabled)
    }
    
    fun onCodeTypesSelected(codeTypes: Collection<Barcode.Type>) {
        val selectedTypes = codeTypes.toCollection(EnumSet.noneOf(Barcode.Type::class.java))
        _uiState.value = _uiState.value.copy(selectedBarcodeTypes = selectedTypes)
    }

    fun setHighlightColor(@ColorInt color: Int) {
        _uiState.value = _uiState.value.copy(highlightColor = color)
    }

    fun setMenuColor(@ColorInt color: Int) {
        _uiState.value = _uiState.value.copy(menuColor = color)
    }
    
    fun setScanResult(result: BarcodeFlowResult) {
        // Don't display result page if flow was canceled
        if (result != BarcodeFlowResult.Canceled) {
            _uiState.value = _uiState.value.copy(scanResult = result)
        }
    }
    
    fun clearScanResult() {
        _uiState.value = _uiState.value.copy(scanResult = null)
    }
}

data class BarcodeUiState(
    val isBatchModeEnabled: Boolean = false,
    val selectedBarcodeTypes: EnumSet<Barcode.Type> = EnumSet.allOf(Barcode.Type::class.java),
    @ColorInt val highlightColor: Int = Color.GREEN,
    @ColorInt val menuColor: Int = Color.BLACK,
    val scanResult: BarcodeFlowResult? = null
)
