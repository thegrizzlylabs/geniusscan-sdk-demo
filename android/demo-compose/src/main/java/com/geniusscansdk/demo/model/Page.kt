package com.geniusscansdk.demo.model

import com.geniusscansdk.core.Quadrangle
import com.geniusscansdk.demo.enhance.Filter
import java.io.File

data class Page(val originalImage: File) {
    var enhancedImage: File? = null
        set(value) {
            field?.delete()
            field = value
        }
    var quadrangle: Quadrangle? = null
    var filter: Filter? = null
    var isDistortionCorrectionEnabled = true
    var isAutomaticallyOriented = false
}
