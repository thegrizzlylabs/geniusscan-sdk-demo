package com.geniusscansdk.demo.camera;

import androidx.annotation.NonNull;

import com.geniusscansdk.core.GeniusScanSDK;
import com.geniusscansdk.core.RotationAngle;
import com.geniusscansdk.demo.model.Page;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class PageRotation implements AutoCloseable {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * Rotates the page on a worker thread. Callback methods are invoked on that same thread.
     */
    public void rotate(@NonNull Page page, @NonNull RotationAngle rotationAngle, @NonNull Callback callback) {
        executor.execute(() -> {
            try {
                String path = page.getOriginalImage().getAbsolutePath();
                // Even if rotation angle is 0, we perform a rotation to apply EXIF orientation.
                GeniusScanSDK.rotateImage(path, path, rotationAngle);
                callback.onSuccess();
            } catch (Exception exception) {
                callback.onFailure(exception);
            }
        });
    }

    @Override
    public void close() {
        executor.shutdownNow();
    }

    public interface Callback {
        void onSuccess();

        void onFailure(@NonNull Exception exception);
    }
}
