package com.geniusscansdk.simpledemo

import android.app.Application
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import com.geniusscansdk.readablecodeflow.ReadableCodeFlowResult
import com.geniusscansdk.structureddata.ReadableCode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.EnumSet

class ReadableCodeViewModel(application: Application) : AndroidViewModel(application) {
    
    private val _uiState = MutableStateFlow(ReadableCodeUiState(menuColor = ContextCompat.getColor(application, R.color.md_theme_primary)))
    val uiState: StateFlow<ReadableCodeUiState> = _uiState.asStateFlow()
    
    fun toggleBatchMode() {
        _uiState.value = _uiState.value.copy(isBatchModeEnabled = !_uiState.value.isBatchModeEnabled)
    }
    
    fun onCodeTypesSelected(codeTypes: Collection<ReadableCode.Type>) {
        val selectedTypes = codeTypes.toCollection(EnumSet.noneOf(ReadableCode.Type::class.java))
        _uiState.value = _uiState.value.copy(selectedCodeTypes = selectedTypes)
    }

    fun setHighlightColor(@ColorInt color: Int) {
        _uiState.value = _uiState.value.copy(highlightColor = color)
    }

    fun setMenuColor(@ColorInt color: Int) {
        _uiState.value = _uiState.value.copy(menuColor = color)
    }
    
    fun setScanResult(result: ReadableCodeFlowResult) {
        // Don't display result page if flow was canceled
        if (result != ReadableCodeFlowResult.Canceled) {
            _uiState.value = _uiState.value.copy(scanResult = result)
        }
    }
    
    fun clearScanResult() {
        _uiState.value = _uiState.value.copy(scanResult = null)
    }
}

data class ReadableCodeUiState(
    val isBatchModeEnabled: Boolean = false,
    val selectedCodeTypes: EnumSet<ReadableCode.Type> = EnumSet.allOf(ReadableCode.Type::class.java),
    @ColorInt val highlightColor: Int = Color.GREEN,
    @ColorInt val menuColor: Int = Color.BLACK,
    val scanResult: ReadableCodeFlowResult? = null
)
