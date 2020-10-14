package com.geniusscansdk.demo.processing;

import android.content.Context;

import com.geniusscansdk.core.ImageType;
import com.geniusscansdk.core.LicenseException;
import com.geniusscansdk.core.ProcessingException;
import com.geniusscansdk.core.Quadrangle;
import com.geniusscansdk.core.ScanProcessor;
import com.geniusscansdk.demo.model.Page;

import java.io.IOException;

/**
 * Created by pnollet on 04/10/2016.
 */

public class PageProcessor {
    private static final String TAG = PageProcessor.class.getSimpleName();

    /**
     * Generate the enhanced image for the given page
     *
     * @param context
     * @param page
     */
    public void processPage(Context context, Page page) throws LicenseException, IOException, ProcessingException {
        String enhancedImagePath = page.getEnhancedImage().getAbsolutePath(context);
        String originalImagePath = page.getOriginalImage().getAbsolutePath(context);

        ImageType imageType = page.getImageType();
        Quadrangle quadrangle = page.getQuadrangle();
        boolean distortionCorrectionEnabled = page.isDistortionCorrectionEnabled();

        ScanProcessor.Parameters parameters = new ScanProcessor.Parameters(quadrangle, imageType, distortionCorrectionEnabled);
        parameters = new ScanProcessor(context).process(originalImagePath, enhancedImagePath, parameters);

        page.setQuadrangle(parameters.getQuadrangle());
        page.setImageType(parameters.getImageType());
    }
}
