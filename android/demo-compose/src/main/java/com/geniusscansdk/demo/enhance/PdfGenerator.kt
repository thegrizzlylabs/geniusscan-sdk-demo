package com.geniusscansdk.demo.enhance

import android.content.Context
import com.geniusscansdk.core.TextLayout
import com.geniusscansdk.demo.model.Page
import com.geniusscansdk.ocr.OcrConfiguration
import com.geniusscansdk.ocr.OcrProcessor
import com.geniusscansdk.pdf.DocumentGenerator
import com.geniusscansdk.pdf.PDFDocument
import com.geniusscansdk.pdf.PDFPage
import com.geniusscansdk.pdf.PDFSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class PdfGenerator private constructor(
    private val context: Context,
    private val pages: List<Page>,
    private val outputFile: File,
    ocrConfiguration: OcrConfiguration?,
    private val onProgress: (Int) -> Unit = {},
) {
    constructor(
        context: Context,
        pages: List<Page>,
        outputFile: File,
        isOcrEnabled: Boolean,
        onProgress: (Int) -> Unit = {},
    ) : this(
        context = context,
        pages = pages,
        outputFile = outputFile,
        ocrConfiguration = if (isOcrEnabled) OcrConfiguration(listOf("en-US")) else null,
        onProgress = onProgress,
    )

    private val documentGenerator = DocumentGenerator(context)
    private val ocrProcessor = ocrConfiguration?.let { configuration ->
        OcrProcessor(
            context,
            configuration,
            object : OcrProcessor.ProgressListener {
                override fun onProgressUpdate(progress: Int) {
                    onProgress(pageProgress + progress / pages.size)
                }
            },
        )
    }

    private var pageProgress = 0

    suspend fun generate() = withContext(Dispatchers.IO) {
        val pdfPages = pages.mapIndexed { pageIndex, page ->
            pageProgress = pageIndex * 100 / pages.size
            onProgress(pageProgress)
            val textLayout: TextLayout? = ocrProcessor?.let { processor ->
                val input = OcrProcessor.Input(page.originalImage, page.quadrangle)
                processor.processImage(input).textLayout
            }
            PDFPage(checkNotNull(page.enhancedImage), A4_SIZE, textLayout)
        }
        val document = PDFDocument(pdfPages, "test")
        val configuration = DocumentGenerator.Configuration(outputFile)
        documentGenerator.generatePDFDocument(document, configuration)
    }

    companion object {
        private val A4_SIZE = PDFSize(8.27, 11.69)
    }
}
