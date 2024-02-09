package com.geniusscansdk.demo.enhance;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

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

public class PdfGenerationTask extends AsyncTask<Void, Integer, Exception> {

    public interface OnPdfGeneratedListener {
        void onPdfGenerated(boolean isSuccess, Exception exception);
    }

    private static final String TAG = PdfGenerationTask.class.getSimpleName();

    private final static PDFSize A4_SIZE = new PDFSize(8.27f, 11.69f); // Size of A4 in inches

    private Context context;
    private File outputFile;
    private List<Page> pages;
    private boolean isOCREnabled;
    private OnPdfGeneratedListener listener;
    private ProgressDialog progressDialog;

    private int pageProgress = 0;

    public PdfGenerationTask(Context context, List<Page> pages, File outputFile, boolean isOCREnabled, OnPdfGeneratedListener listener) {
        this.context = context;
        this.outputFile = outputFile;
        this.pages = pages;
        this.isOCREnabled = isOCREnabled;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        if (isOCREnabled) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMax(100);
            progressDialog.setMessage("OCR in progress");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    @Override
    protected Exception doInBackground(Void... params) {
        OcrProcessor ocrProcessor = null;

        if (isOCREnabled) {
            OcrConfiguration ocrConfiguration = new OcrConfiguration(Arrays.asList("en-US"));
            ocrProcessor = new OcrProcessor(context, ocrConfiguration, progress -> {
                publishProgress(pageProgress + progress / pages.size());
            });
        }

        ArrayList<PDFPage> pdfPages = new ArrayList<>();
        int pageIndex = 0;
        for (Page page : pages) {
            pageProgress = pageIndex * 100 / pages.size();
            File image = page.getEnhancedImage();

            TextLayout textLayout = null;
            if (isOCREnabled) {
                try {
                    OcrResult result = ocrProcessor.processImage(image);
                    textLayout = result.textLayout;
                } catch (Exception e) {
                    return new Exception("OCR processing failed", e);
                }
            }

            // Export all pages in A4
            pdfPages.add(new PDFPage(image, A4_SIZE, textLayout));
            pageIndex++;
        }

        // Here we don't protect the PDF document with a password
        PDFDocument pdfDocument = new PDFDocument(pdfPages, /* title = */ "test");
        try {
            DocumentGenerator.Configuration configuration = new DocumentGenerator.Configuration(outputFile);
            new DocumentGenerator(context).generatePDFDocument(pdfDocument, configuration);
            return null;
        } catch (Exception e) {
            return e;
        }
    }

    @Override
    protected void onPostExecute(Exception error) {
        super.onPostExecute(error);
        listener.onPdfGenerated(error == null, error);
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (progressDialog != null) {
            progressDialog.setProgress(values[0]);
        }
    }
}
