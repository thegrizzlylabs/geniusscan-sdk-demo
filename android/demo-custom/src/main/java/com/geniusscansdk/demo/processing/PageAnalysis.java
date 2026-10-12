package com.geniusscansdk.demo.processing;

import android.content.Context;

import androidx.annotation.NonNull;

import com.geniusscansdk.core.DocumentDetector;
import com.geniusscansdk.core.FileInput;
import com.geniusscansdk.core.Quadrangle;
import com.geniusscansdk.demo.model.Page;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class PageAnalysis implements AutoCloseable {
    private final Context context;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public PageAnalysis(@NonNull Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * Detects the document on a worker thread. Callback methods are invoked on that same thread.
     */
    public void analyze(@NonNull Page page, @NonNull Callback callback) {
        executor.execute(() -> {
            try (DocumentDetector documentDetector = DocumentDetector.create(context)) {
                Quadrangle quadrangle = documentDetector
                        .detectDocument(new FileInput(page.getOriginalImage()))
                        .getQuadrangle();
                callback.onSuccess(quadrangle);
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
        void onSuccess(@NonNull Quadrangle quadrangle);

        void onFailure(@NonNull Exception exception);
    }
}
