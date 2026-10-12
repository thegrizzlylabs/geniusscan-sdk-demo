package com.geniusscansdk.demo.enhance

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.geniusscansdk.core.GeniusScanSDK
import com.geniusscansdk.core.RotationAngle
import com.geniusscansdk.core.ScanProcessor
import com.geniusscansdk.demo.model.Page
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class PageProcessing(context: Context) : AutoCloseable {
    private val context = context.applicationContext
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()
    private val dispatcher = executor.asCoroutineDispatcher()
    private var pageProcessingEngine: PageProcessingEngine? = null

    suspend fun process(page: Page, rotationAngle: RotationAngle? = null): ImageBitmap =
        withContext(dispatcher) {
            rotationAngle?.let { angle ->
                page.quadrangle = page.quadrangle?.rotate(angle)
                val path = page.originalImage.absolutePath
                GeniusScanSDK.rotateImage(path, path, angle)
            }
            val engine = pageProcessingEngine ?: PageProcessingEngine(context).also {
                pageProcessingEngine = it
            }
            engine.process(page)
            BitmapFactory.decodeFile(checkNotNull(page.enhancedImage).absolutePath).asImageBitmap()
        }

    override fun close() {
        executor.execute {
            pageProcessingEngine?.close()
        }
        executor.shutdown()
    }
}

class PageProcessingEngine(private val outputDirectory: File, private val scanProcessor: ScanProcessor) : AutoCloseable {

    constructor(context: Context) : this(checkNotNull(context.getExternalFilesDir(null)), ScanProcessor(context))

    fun process(page: Page) {
        val configuration = ScanProcessor.Configuration(
            page.quadrangle?.let(ScanProcessor.PerspectiveCorrection::withQuadrangle)
                ?: ScanProcessor.PerspectiveCorrection.automatic(),
            ScanProcessor.CurvatureCorrection.create(page.isDistortionCorrectionEnabled),
            page.filter?.enhancement ?: ScanProcessor.Enhancement.automatic(),
            if (page.isAutomaticallyOriented) {
                ScanProcessor.Rotation.none()
            } else {
                ScanProcessor.Rotation.automatic()
            },
            ScanProcessor.Readability.disabled(),
            ScanProcessor.OutputConfiguration.file(outputDirectory),
        )
        val result: ScanProcessor.Result<File> = scanProcessor.process(
            page.originalImage,
            configuration,
        )

        var appliedQuadrangle = result.appliedQuadrangle
        val appliedRotation = result.appliedRotation
        if (appliedRotation != RotationAngle.ROTATION_0) {
            val imagePath = page.originalImage.absolutePath
            GeniusScanSDK.rotateImage(imagePath, imagePath, appliedRotation)
            appliedQuadrangle = appliedQuadrangle.rotate(appliedRotation)
        }

        page.quadrangle = appliedQuadrangle
        page.isAutomaticallyOriented = true
        page.enhancedImage = result.output
    }

    override fun close() {
        scanProcessor.close()
    }
}
