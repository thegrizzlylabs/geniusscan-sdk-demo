package com.geniusscansdk.demo.enhance;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.geniusscansdk.core.GeniusScanSDK;
import com.geniusscansdk.core.Logger;
import com.geniusscansdk.core.TextLayout;
import com.geniusscansdk.demo.R;
import com.geniusscansdk.demo.model.Page;
import com.geniusscansdk.ocr.OCREngineProgressListener;
import com.geniusscansdk.ocr.OcrConfiguration;
import com.geniusscansdk.ocr.OcrProcessor;
import com.geniusscansdk.ocr.OcrResult;
import com.geniusscansdk.pdf.PDFDocument;
import com.geniusscansdk.pdf.PDFGenerator;
import com.geniusscansdk.pdf.PDFGeneratorConfiguration;
import com.geniusscansdk.pdf.PDFGeneratorError;
import com.geniusscansdk.pdf.PDFImageProcessor;
import com.geniusscansdk.pdf.PDFPage;
import com.geniusscansdk.pdf.PDFSize;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by guillaume on 25/10/16.
 */

public class PdfGenerationTask extends AsyncTask<Void, Integer, Exception> {

    public interface OnPdfGeneratedListener {
        void onPdfGenerated(boolean isSuccess, Exception exception);
    }

    private static final String TAG = PdfGenerationTask.class.getSimpleName();

    private final static PDFSize A4_SIZE = new PDFSize(8.27f, 11.69f); // Size of A4 in inches

    private Context context;
    private String outputFilePath;
    private List<Page> pages;
    private boolean isOCREnabled;
    private OnPdfGeneratedListener listener;
    private ProgressDialog progressDialog;

    private int pageProgress = 0;

    public PdfGenerationTask(Context context, List<Page> pages, String outputFilePath, boolean isOCREnabled, OnPdfGeneratedListener listener) {
        this.context = context;
        this.outputFilePath = outputFilePath;
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
        Logger logger = GeniusScanSDK.getLogger();
        OcrProcessor ocrProcessor = null;

        if (isOCREnabled) {
            OcrConfiguration ocrConfiguration = new OcrConfiguration(Arrays.asList("eng"), getTessdataDirectory());
            ocrProcessor = new OcrProcessor(context, ocrConfiguration, new OCREngineProgressListener() {
                @Override
                public void updateProgress(int progress) {
                    publishProgress(pageProgress + progress / pages.size());
                }
            });
            try {
                copyTessdataFiles();
            } catch (IOException e) {
                return new IOException("Cannot copy tessdata", e);
            }
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
            pdfPages.add(new PDFPage(image.getAbsolutePath(), A4_SIZE, textLayout));
            pageIndex++;
        }

        // Here we don't protect the PDF document with a password
        PDFDocument pdfDocument = new PDFDocument("test", null, null, new Date(), new Date(), pdfPages);

        PDFGenerator generator = PDFGenerator.createWithDocument(pdfDocument, new PDFGeneratorConfiguration(null, false), new PDFNoopImageProcessor(), logger);
        PDFGeneratorError error = generator.generatePDF(outputFilePath);
        if (error == PDFGeneratorError.SUCCESS) {
            return null;
        } else {
            return new Exception("PDF generation failed with code: " + error);
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

    private void copyTessdataFiles() throws IOException {
        File tessdataDir = getTessdataDirectory();

        if (tessdataDir.exists()) {
            return;
        }

        tessdataDir.mkdir();

        InputStream in = context.getResources().openRawResource(R.raw.eng);
        File engFile = new File(tessdataDir, "eng.traineddata");
        OutputStream out = new FileOutputStream(engFile);

        byte[] buffer = new byte[1024];
        int len = in.read(buffer);
        while (len != -1) {
            out.write(buffer, 0, len);
            len = in.read(buffer);
        }
    }

    private File getTessdataDirectory() {
        return new File(context.getExternalFilesDir(null), "tessdata");
    }

    private class PDFNoopImageProcessor extends PDFImageProcessor {
        @Override
        public String process(String inputFilePath) {
            return inputFilePath;
        }
    }
}
