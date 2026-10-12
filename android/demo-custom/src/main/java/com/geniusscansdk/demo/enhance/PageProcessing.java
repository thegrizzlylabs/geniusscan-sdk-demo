package com.geniusscansdk.demo.enhance;

import android.content.Context;

import androidx.annotation.NonNull;

import com.geniusscansdk.core.GeniusScanSDK;
import com.geniusscansdk.core.LicenseException;
import com.geniusscansdk.core.ProcessingException;
import com.geniusscansdk.core.Quadrangle;
import com.geniusscansdk.core.RotationAngle;
import com.geniusscansdk.core.ScanProcessor;
import com.geniusscansdk.demo.model.Page;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class PageProcessing implements AutoCloseable {
    private final Context context;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private PageProcessingEngine pageProcessingEngine;

    public PageProcessing(@NonNull Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * Rotates and reprocesses the page on a worker thread. Callback methods are invoked on that
     * same thread.
     */
    public void rotateAndProcess(@NonNull Page page, @NonNull RotationAngle angle, @NonNull Callback callback) {
        executor.execute(() -> {
            try {
                if (pageProcessingEngine == null) {
                    pageProcessingEngine = new PageProcessingEngine(context);
                }
                if (page.getQuadrangle() != null) {
                    page.setQuadrangle(page.getQuadrangle().rotate(angle));
                }
                String imagePath = page.getOriginalImage().getAbsolutePath();
                GeniusScanSDK.rotateImage(imagePath, imagePath, angle);
                pageProcessingEngine.process(page);
                callback.onSuccess();
            } catch (Exception exception) {
                callback.onFailure(exception);
            }
        });
    }

    /**
     * Enhances the page on a worker thread. Callback methods are invoked on that same thread.
     */
    public void process(@NonNull Page page, @NonNull Callback callback) {
        executor.execute(() -> {
            try {
                if (pageProcessingEngine == null) {
                    pageProcessingEngine = new PageProcessingEngine(context);
                }
                pageProcessingEngine.process(page);
                callback.onSuccess();
            } catch (Exception exception) {
                callback.onFailure(exception);
            }
        });
    }

    @Override
    public void close() {
        executor.execute(() -> {
            if (pageProcessingEngine != null) {
                pageProcessingEngine.close();
            }
        });
        executor.shutdown();
    }

    public interface Callback {
        void onSuccess();

        void onFailure(@NonNull Exception exception);
    }
}

final class PageProcessingEngine implements AutoCloseable {
    private final File outputDirectory;
    private final ScanProcessor scanProcessor;

    public PageProcessingEngine(Context context) {
        outputDirectory = context.getExternalFilesDir(null);
        scanProcessor = new ScanProcessor(context);
    }

    public void process(Page page) throws LicenseException, ProcessingException {
        ScanProcessor.Configuration<File> configuration = new ScanProcessor.Configuration<>(
                page.getQuadrangle() == null ? ScanProcessor.PerspectiveCorrection.automatic() : ScanProcessor.PerspectiveCorrection.withQuadrangle(page.getQuadrangle()),
                ScanProcessor.CurvatureCorrection.create(page.isDistortionCorrectionEnabled()),
                page.getFilter() == null ? ScanProcessor.Enhancement.automatic() : page.getFilter().toEnhancement(),
                page.isAutomaticallyOriented() ? ScanProcessor.Rotation.none() : ScanProcessor.Rotation.automatic(),
                ScanProcessor.Readability.disabled(),
                ScanProcessor.OutputConfiguration.file(outputDirectory)
        );

        ScanProcessor.Result<File> result = scanProcessor.process(page.getOriginalImage(), configuration);

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
        if (!page.isAutomaticallyOriented()) {
            page.setAutomaticallyOriented(true);
        }
        page.setEnhancedImage(result.output);
    }

    @Override
    public void close() {
        scanProcessor.close();
    }
}
