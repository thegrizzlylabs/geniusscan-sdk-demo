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
import com.geniusscansdk.core.ScanProcessor.OutputConfiguration;
import com.geniusscansdk.core.ScanProcessor.PerspectiveCorrection;
import com.geniusscansdk.core.ScanProcessor.Result;
import com.geniusscansdk.core.ScanProcessor.Rotation;
import com.geniusscansdk.demo.model.Page;

import java.io.File;
import java.io.IOException;

public class PageProcessor {

    public void processPage(Context context, Page page) throws LicenseException, ProcessingException {
        Configuration<File> configuration = new Configuration<>(
                page.getQuadrangle() == null ? PerspectiveCorrection.automatic() : PerspectiveCorrection.withQuadrangle(page.getQuadrangle()),
                CurvatureCorrection.create(page.isDistortionCorrectionEnabled()),
                page.getFilterType() == null ? Enhancement.automatic() : Enhancement.withFilter(page.getFilterType()),
                page.isAutomaticallyOriented() ? Rotation.none() : Rotation.automatic(),
                OutputConfiguration.file(context.getExternalFilesDir(null))
        );

        Result<File> result = new ScanProcessor(context).process(page.getOriginalImage(), configuration);

        Quadrangle appliedQuadrangle = result.appliedQuadrangle;
        RotationAngle appliedRotation = result.appliedRotation;
        if (appliedRotation != RotationAngle.ROTATION_0) {
            try {
                String imagePath = page.getOriginalImage().getAbsolutePath();
                GeniusScanSDK.rotateImage(imagePath, imagePath, appliedRotation);
                appliedQuadrangle = appliedQuadrangle.rotate(appliedRotation);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        page.setQuadrangle(appliedQuadrangle);
        page.setFilterType(result.appliedFilter);
        if (!page.isAutomaticallyOriented()) {
            page.setAutomaticallyOriented(true);
        }
        page.setEnhancedImage(result.output);
    }
}
