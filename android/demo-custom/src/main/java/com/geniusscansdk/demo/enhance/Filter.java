package com.geniusscansdk.demo.enhance;

import com.geniusscansdk.core.FilterConfiguration;
import com.geniusscansdk.core.FilterConfiguration.Color.Palette;
import com.geniusscansdk.core.ScanProcessor.Enhancement;
import com.geniusscansdk.core.ScanProcessor.FilterStyle;

public enum Filter {
    NONE,
    AUTO,
    BLACK_AND_WHITE,
    COLOR,
    PHOTO;

    public Enhancement toEnhancement() {
        switch(this) {
            case NONE: return Enhancement.none();
            case AUTO: return Enhancement.automatic();
            case PHOTO: return Enhancement.withFilterConfiguration(FilterConfiguration.photo());
            case BLACK_AND_WHITE: return Enhancement.automatic(FilterStyle.DOCUMENT, Palette.GRAYSCALE);
            case COLOR: return Enhancement.automatic(FilterStyle.DOCUMENT, Palette.COLOR);
            default: throw new IllegalArgumentException();
        }
    }
}
