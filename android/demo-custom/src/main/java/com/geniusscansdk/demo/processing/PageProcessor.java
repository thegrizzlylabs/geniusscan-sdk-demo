package com.geniusscansdk.demo.processing;

import android.content.Context;

import com.geniusscansdk.core.GeniusScanSDK;
import com.geniusscansdk.core.LicenseException;
import com.geniusscansdk.core.ProcessingException;
import com.geniusscansdk.core.Quadrangle;
import com.geniusscansdk.core.RotationAngle;
import com.geniusscansdk.core.ScanProcessor;
import com.geniusscansdk.core.ScanProcessor.Configuration;
import com.geniusscansdk.core.ScanProcessor.CurvatureCorrection;
import com.geniusscansdk.core.ScanProcessor.Enhancement;
import com.geniusscansdk.core.ScanProcessor.OutputParameters;
import com.geniusscansdk.core.ScanProcessor.PerspectiveCorrection;
import com.geniusscansdk.core.ScanProcessor.Rotation;
import com.geniusscansdk.demo.model.Page;

import java.io.IOException;

public class PageProcessor {

    public void processPage(Context context, Page page) throws LicenseException, ProcessingException {
        String enhancedImagePath = page.getEnhancedImage().getAbsolutePath(context);
        String originalImagePath = page.getOriginalImage().getAbsolutePath(context);

        Configuration configuration = new Configuration(
                page.getQuadrangle() == null ? PerspectiveCorrection.automatic() : PerspectiveCorrection.withQuadrangle(page.getQuadrangle()),
                CurvatureCorrection.create(page.isDistortionCorrectionEnabled()),
                page.getFilterType() == null ? Enhancement.automatic() : Enhancement.withFilter(page.getFilterType()),
                page.isAutomaticallyOriented() ? Rotation.none() : Rotation.automatic()
        );

        OutputParameters outputParameters = new ScanProcessor(context).process(originalImagePath, enhancedImagePath, configuration);

        Quadrangle appliedQuadrangle = outputParameters.appliedQuadrangle;
        RotationAngle appliedRotation = outputParameters.appliedRotation;
        if (appliedRotation != RotationAngle.ROTATION_0) {
            try {
                GeniusScanSDK.rotateImage(originalImagePath, originalImagePath, appliedRotation);
                appliedQuadrangle = appliedQuadrangle.rotate(appliedRotation);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        page.setQuadrangle(appliedQuadrangle);
        page.setFilterType(outputParameters.appliedFilter);
        if (!page.isAutomaticallyOriented()) {
            page.setAutomaticallyOriented(true);
        }
    }
}
