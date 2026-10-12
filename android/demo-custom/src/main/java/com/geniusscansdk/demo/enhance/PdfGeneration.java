package com.geniusscansdk.demo.enhance;

import android.content.Context;

import androidx.annotation.NonNull;

import com.geniusscansdk.core.TextLayout;
import com.geniusscansdk.demo.model.Page;
import com.geniusscansdk.ocr.OcrConfiguration;
import com.geniusscansdk.ocr.OcrProcessor;
import com.geniusscansdk.ocr.OcrResult;
import com.geniusscansdk.pdf.DocumentGenerator;
import com.geniusscansdk.pdf.PDFDocument;
import com.geniusscansdk.pdf.PDFPage;
import com.geniusscansdk.pdf.PDFSize;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class PdfGeneration implements AutoCloseable {
    private static final PDFSize A4_SIZE = new PDFSize(210.0 / 25.4, 297.0 / 25.4);

    private final Context context;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public PdfGeneration(@NonNull Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * Generates the PDF on a worker thread. Callback methods are invoked on that same thread.
     */
    public void generate(
            @NonNull List<Page> pages,
            @NonNull File outputFile,
            boolean isOcrEnabled,
            @NonNull Callback callback
    ) {
        executor.execute(() -> {
            try {
                generatePdf(pages, outputFile, isOcrEnabled, callback);
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

    private void generatePdf(
            List<Page> pages,
            File outputFile,
            boolean isOcrEnabled,
            Callback callback
    ) throws Exception {
        OcrProcessor ocrProcessor = null;
        int[] pageProgress = {0};
        if (isOcrEnabled) {
            OcrConfiguration ocrConfiguration = new OcrConfiguration(Arrays.asList("en-US"));
            ocrProcessor = new OcrProcessor(context, ocrConfiguration, progress -> {
                callback.onProgress(pageProgress[0] + progress / pages.size());
            });
        }

        ArrayList<PDFPage> pdfPages = new ArrayList<>();
        for (int pageIndex = 0; pageIndex < pages.size(); pageIndex++) {
            Page page = pages.get(pageIndex);
            pageProgress[0] = pageIndex * 100 / pages.size();

            TextLayout textLayout = null;
            if (isOcrEnabled) {
                try {
                    OcrProcessor.Input ocrInput = new OcrProcessor.Input(
                            page.getOriginalImage(),
                            page.getQuadrangle()
                    );
                    OcrResult result = ocrProcessor.processImage(ocrInput);
                    textLayout = result.textLayout;
                } catch (Exception exception) {
                    throw new Exception("OCR processing failed", exception);
                }
            }

            // Export all pages in A4.
            pdfPages.add(new PDFPage(page.getEnhancedImage(), A4_SIZE, textLayout));
            if (isOcrEnabled) {
                callback.onProgress((pageIndex + 1) * 100 / pages.size());
            }
        }

        PDFDocument pdfDocument = new PDFDocument(pdfPages, /* title = */ "test");
        DocumentGenerator.Configuration configuration = new DocumentGenerator.Configuration(outputFile);
        new DocumentGenerator(context).generatePDFDocument(pdfDocument, configuration);
    }

    public interface Callback {
        void onProgress(int progress);

        void onSuccess();

        void onFailure(@NonNull Exception exception);
    }
}
