package com.geniusscansdk.simpledemo

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.geniusscansdk.scanflow.ScanResult
import com.geniusscansdk.structureddata.ReadableCode
import com.geniusscansdk.structureddata.StructuredDataReceipt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class StructuredDataUiState(
    val pages: List<Page> = listOf()
)

data class Page(
    val image: Uri? = null,
    val receipt: StructuredDataReceipt? = null,
    val barcodes: List<ReadableCode>? = listOf()
)

class StructuredDataViewModel: ViewModel() {

    private val viewModelState = MutableStateFlow(StructuredDataUiState())
    val uiState = viewModelState.asStateFlow()

    fun showResult(scanResult: ScanResult) {
        viewModelState.update {
            it.copy(
                pages = scanResult.scans?.map { scan ->
                    Page(
                        image = Uri.fromFile(scan.enhancedImageFile),
                        receipt = scan.structuredDataResult?.receipt,
                        barcodes = scan.structuredDataResult?.readableCodes
                    )} ?: listOf()
            )
        }
    }

    fun clearPages() {
        viewModelState.update {
            it.copy(pages = listOf())
        }
    }
}
