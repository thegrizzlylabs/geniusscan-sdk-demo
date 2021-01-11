package com.geniusscansdk.demo.processing;

import android.content.Context;

import com.geniusscansdk.core.LicenseException;
import com.geniusscansdk.core.ProcessingException;
import com.geniusscansdk.core.ScanProcessor;
import com.geniusscansdk.core.ScanProcessor.Configuration;
import com.geniusscansdk.core.ScanProcessor.CurvatureCorrection;
import com.geniusscansdk.core.ScanProcessor.Enhancement;
import com.geniusscansdk.core.ScanProcessor.OutputParameters;
import com.geniusscansdk.core.ScanProcessor.PerspectiveCorrection;
import com.geniusscansdk.demo.model.Page;

public class PageProcessor {

    public void processPage(Context context, Page page) throws LicenseException, ProcessingException {
        String enhancedImagePath = page.getEnhancedImage().getAbsolutePath(context);
        String originalImagePath = page.getOriginalImage().getAbsolutePath(context);

        Configuration configuration = new Configuration(
                page.getQuadrangle() == null ? PerspectiveCorrection.automatic() : PerspectiveCorrection.withQuadrangle(page.getQuadrangle()),
                CurvatureCorrection.create(page.isDistortionCorrectionEnabled()),
                page.getFilterType() == null ? Enhancement.automatic() : Enhancement.withFilter(page.getFilterType())
        );

        OutputParameters outputParameters = new ScanProcessor(context).process(originalImagePath, enhancedImagePath, configuration);

        page.setQuadrangle(outputParameters.appliedQuadrangle);
        page.setFilterType(outputParameters.appliedFilter);
    }
}
