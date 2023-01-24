package com.geniusscansdk.demo.model;


import com.geniusscansdk.core.FilterType;
import com.geniusscansdk.core.Quadrangle;

import java.io.File;
import java.io.Serializable;

public class Page implements Serializable {

    public Page(File originalImage) {
        this.originalImage = originalImage;
    }

    private final File originalImage;
    private File enhancedImage;
    private Quadrangle quadrangle;
    private FilterType filterType;
    private boolean distortionCorrectionEnabled = true;
    private boolean automaticallyOriented = false;

    public File getOriginalImage() {
        return originalImage;
    }

    public File getEnhancedImage() {
        return enhancedImage;
    }

    public void setEnhancedImage(File enhancedImage) {
        if (this.enhancedImage != null) {
            this.enhancedImage.delete();
        }
        this.enhancedImage = enhancedImage;
    }

    public void setQuadrangle(Quadrangle quadrangle) {
        this.quadrangle = quadrangle;
    }

    public Quadrangle getQuadrangle() {
        return quadrangle;
    }

    public void setFilterType(FilterType filterType) {
        this.filterType = filterType;
    }

    public FilterType getFilterType() {
        return filterType;
    }

    public void setDistortionCorrectionEnabled(boolean distortionCorrectionEnabled) {
        this.distortionCorrectionEnabled = distortionCorrectionEnabled;
    }

    public boolean isDistortionCorrectionEnabled() {
        return distortionCorrectionEnabled;
    }

    public boolean isAutomaticallyOriented() {
        return automaticallyOriented;
    }

    public void setAutomaticallyOriented(boolean automaticallyOriented) {
        this.automaticallyOriented = automaticallyOriented;
    }
}
