package com.geniusscansdk.demo.enhance

import com.geniusscansdk.core.FilterConfiguration
import com.geniusscansdk.core.FilterConfiguration.Color.Palette
import com.geniusscansdk.core.ScanProcessor.Enhancement
import com.geniusscansdk.core.ScanProcessor.FilterStyle
import com.geniusscansdk.demo.R

enum class Filter(val displayId: Int, val enhancement: Enhancement) {
    NONE(R.string.none, Enhancement.none()),
    AUTO(R.string.automatic, Enhancement.automatic()),
    BLACK_AND_WHITE(R.string.black_white, Enhancement.automatic(FilterStyle.DOCUMENT, Palette.GRAYSCALE)),
    COLOR(R.string.color, Enhancement.automatic(FilterStyle.DOCUMENT, Palette.COLOR)),
    PHOTO(R.string.photo, Enhancement.withFilterConfiguration(FilterConfiguration.photo()));
}
