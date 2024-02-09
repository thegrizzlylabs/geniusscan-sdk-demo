package com.geniusscansdk.simpledemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RawRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.geniusscansdk.core.LicenseException;
import com.geniusscansdk.scanflow.ScanConfiguration;
import com.geniusscansdk.scanflow.ScanFlow;
import com.geniusscansdk.scanflow.ScanResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("GS SDK Simple Demo");

        // This code shows how to initialize the SDK with a license key.
        // Without a license key, the SDK runs for 60 seconds and then the app needs to be restarted.
        //
        // GeniusScanSDK.setLicenseKey(this, "<Your license key>");

        findViewById(R.id.scan_camera_button).setOnClickListener(view -> scanFromCamera());
        findViewById(R.id.scan_image_button).setOnClickListener(view -> scanFromImage());
        findViewById(R.id.photo_picker_button).setOnClickListener(view -> scanFromPicker());

        TextView appVersionView = findViewById(R.id.app_version);
        appVersionView.setText("v" + BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")");
    }

    private ScanConfiguration createBaseConfiguration() {
        ScanConfiguration scanConfiguration = new ScanConfiguration();
        scanConfiguration.multiPage = true;
        scanConfiguration.multiPageFormat = ScanConfiguration.MultiPageFormat.PDF;
        scanConfiguration.pdfPageSize = ScanConfiguration.PdfPageSize.FIT;
        scanConfiguration.pdfMaxScanDimension = 2000;
        scanConfiguration.jpegQuality = 60;
        scanConfiguration.postProcessingActions = ScanConfiguration.Action.ALL;
        scanConfiguration.flashButtonHidden = false;
        scanConfiguration.defaultFlashMode = ScanConfiguration.FlashMode.AUTO;
        scanConfiguration.backgroundColor = Color.WHITE;
        scanConfiguration.foregroundColor = ContextCompat.getColor(this, R.color.colorPrimary);
        scanConfiguration.highlightColor = ContextCompat.getColor(this, R.color.colorAccent);

        ScanConfiguration.OcrConfiguration ocrConfiguration = new ScanConfiguration.OcrConfiguration();
        ocrConfiguration.languages = Arrays.asList("en-US");

        scanConfiguration.ocrConfiguration = ocrConfiguration;

        return scanConfiguration;
    }

    private void scanFromCamera() {
        ScanConfiguration scanConfiguration = createBaseConfiguration();
        scanConfiguration.source = ScanConfiguration.Source.CAMERA;
        startScanning(scanConfiguration);
    }

    private void scanFromImage() {
        ScanConfiguration scanConfiguration = createBaseConfiguration();
        scanConfiguration.source = ScanConfiguration.Source.IMAGE;

        scanConfiguration.sourceImage = new File(getExternalCacheDir(), "temp.jpg");
        copyFileFromResource(R.raw.scan, scanConfiguration.sourceImage);

        startScanning(scanConfiguration);
    }

    private void scanFromPicker() {
        ScanConfiguration scanConfiguration = createBaseConfiguration();
        scanConfiguration.source = ScanConfiguration.Source.GALLERY;
        startScanning(scanConfiguration);
    }

    private void startScanning(ScanConfiguration scanConfiguration) {
        ScanFlow.scanWithConfiguration(MainActivity.this, scanConfiguration);
    }

    private void copyFileFromResource(@RawRes int fileResId, File destinationFile) {
        if (destinationFile.exists()) {
            return;
        }

        byte[] buff = new byte[1024];
        int read;

        try (InputStream in = getResources().openRawResource(fileResId);
             FileOutputStream out = new FileOutputStream(destinationFile)) {
            while ((read = in.read(buff)) > 0) {
                out.write(buff, 0, read);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == ScanFlow.SCAN_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            try {
                ScanResult result = ScanFlow.getScanResultFromActivityResult(data);
                Uri uri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".fileprovider", result.multiPageDocument);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            } catch (LicenseException e) {
                if (e.errorCode == LicenseException.ErrorCode.ExpiredDemo) {
                    new AlertDialog.Builder(this)
                            .setMessage(e.getMessage())
                            .setPositiveButton("Restart", (dialog, which) -> restartApp())
                            .show();
                } else {
                    // The license key is invalid or expired, either ask the user to update the app or provide a fallback
                    new AlertDialog.Builder(this)
                            .setMessage("Please update to the latest version.")
                            .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {})
                            .show();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error during scan flow", e);
                new AlertDialog.Builder(this)
                        .setMessage("An error occurred: " + e.getMessage())
                        .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {})
                        .show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void restartApp() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

        Runtime.getRuntime().exit(0);
    }
}
