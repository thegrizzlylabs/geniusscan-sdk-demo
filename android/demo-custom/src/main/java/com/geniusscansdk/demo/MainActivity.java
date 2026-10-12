package com.geniusscansdk.demo;

import android.content.Intent;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.geniusscansdk.core.GeniusScanSDK;
import com.geniusscansdk.demo.camera.ScanActivity;
import com.geniusscansdk.demo.enhance.PdfGeneration;
import com.geniusscansdk.demo.model.DocumentManager;
import com.geniusscansdk.demo.model.Page;

import java.io.File;
import java.util.List;

/**
 * Created by guillaume on 29/09/16.
 */

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView pageCountView;
    private Button shareButton;
    private SwitchCompat ocrSwitch;
    private PdfGeneration pdfGeneration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.main_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("GS SDK Custom Demo");

        // This code shows how to initialize the SDK with a license key.
        // Without a license key, the SDK runs for 60 seconds and then the app needs to be restarted.
        if (!BuildConfig.GSSDK_CUSTOM_LICENSE_KEY.isEmpty()) {
            GeniusScanSDK.setLicenseKey(this, BuildConfig.GSSDK_CUSTOM_LICENSE_KEY, /*autorefresh*/ true);
        }

        pageCountView = findViewById(R.id.page_count);

        shareButton = findViewById(R.id.share_button);
        shareButton.setOnClickListener(v -> shareDocument());

        ocrSwitch = findViewById(R.id.ocr_switch);
        pdfGeneration = new PdfGeneration(this);

        Button cameraButton = findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ScanActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();

        int pageCount = DocumentManager.getInstance(this).getPages().size();
        pageCountView.setText(getResources().getQuantityString(R.plurals.page_count, pageCount, pageCount));
        shareButton.setEnabled(pageCount > 0);
        ocrSwitch.setEnabled(pageCount > 0);
    }

    @Override
    protected void onDestroy() {
        pdfGeneration.close();
        super.onDestroy();
    }

    private void shareDocument() {
        List<Page> pages = DocumentManager.getInstance(this).getPages();
        final File outputFile = new File(getExternalCacheDir(), "test.pdf");
        boolean isOcrEnabled = ocrSwitch.isChecked();
        ProgressDialog progressDialog = isOcrEnabled ? createOcrProgressDialog() : null;
        pdfGeneration.generate(pages, outputFile, isOcrEnabled, new PdfGeneration.Callback() {
            @Override
            public void onProgress(int progress) {
                runOnUiThread(() -> progressDialog.setProgress(progress));
            }

            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    dismissProgressDialog(progressDialog);
                    viewPdf(outputFile);
                });
            }

            @Override
            public void onFailure(Exception exception) {
                runOnUiThread(() -> {
                    dismissProgressDialog(progressDialog);
                    Log.e(TAG, "Error generating PDF", exception);
                    Toast.makeText(MainActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private ProgressDialog createOcrProgressDialog() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(100);
        progressDialog.setMessage("OCR in progress");
        progressDialog.setCancelable(false);
        progressDialog.show();
        return progressDialog;
    }

    private void dismissProgressDialog(ProgressDialog progressDialog) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void viewPdf(File outputFile) {
        Uri uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", outputFile);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }
}
