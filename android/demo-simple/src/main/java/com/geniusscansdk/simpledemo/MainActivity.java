package com.geniusscansdk.simpledemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.geniusscansdk.scanflow.ScanConfiguration;
import com.geniusscansdk.scanflow.ScanFlow;
import com.geniusscansdk.scanflow.ScanResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.EnumSet;

import androidx.annotation.Nullable;
import androidx.annotation.RawRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import static com.geniusscansdk.scanflow.ScanConfiguration.Action.EDIT_FILTER;
import static com.geniusscansdk.scanflow.ScanConfiguration.Action.ROTATE;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.scan_camera_button).setOnClickListener(view -> scanFromCamera());
        findViewById(R.id.scan_image_button).setOnClickListener(view -> scanFromImage());
    }

    private ScanConfiguration createBaseConfiguration() {
        ScanConfiguration scanConfiguration = new ScanConfiguration();
        scanConfiguration.multiPage = true;
        scanConfiguration.pdfPageSize = ScanConfiguration.PdfPageSize.FIT;
        scanConfiguration.pdfMaxScanDimension = 2000;
        scanConfiguration.jpegQuality = 60;
        scanConfiguration.postProcessingActions = EnumSet.of(ROTATE, EDIT_FILTER);
        scanConfiguration.flashButtonHidden = false;
        scanConfiguration.defaultFlashMode = ScanConfiguration.FlashMode.AUTO;
        scanConfiguration.backgroundColor = Color.WHITE;
        scanConfiguration.foregroundColor = ContextCompat.getColor(this, R.color.colorPrimary);
        scanConfiguration.highlightColor = ContextCompat.getColor(this, R.color.colorAccent);

        ScanConfiguration.OcrConfiguration ocrConfiguration = new ScanConfiguration.OcrConfiguration();
        ocrConfiguration.languages = Arrays.asList("eng");
        ocrConfiguration.languagesDirectory = getTessdataDirectory();

        scanConfiguration.ocrConfiguration = ocrConfiguration;
        // FIXME : Avoid copying tessdata files in every demo apps.
        copyFileFromResource(R.raw.eng, new File(getTessdataDirectory(), "eng.traineddata"));

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

    private void startScanning(ScanConfiguration scanConfiguration) {
        ////////////////////////////////////////////////////////////////////////////////////////////////////
        // This code shows how to initialize the SDK with a license key.
        // Without a license key, the SDK runs for 60 seconds and then the app needs to be restarted.
        //
        //         try {
        //            // Replace this key by your key
        //            ScanFlow.init(this, "<Your license key>");
        //         } catch(LicenseException e) {
        //            new AlertDialog.Builder(this)
        //                    .setMessage("This version is not valid anymore. Please update to the latest version.")
        //                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
        //                        @Override
        //                        public void onClick(DialogInterface dialog, int which) {
        //                            finish();
        //                        }
        //                    })
        //                    .show();
        //         }
        ////////////////////////////////////////////////////////////////////////////////////////////////////

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

    private File getTessdataDirectory() {
        File directory = new File(getExternalFilesDir(null), "tessdata");
        directory.mkdirs();
        return directory;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == ScanFlow.SCAN_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            try {
                ScanResult result = ScanFlow.getScanResultFromActivityResult(data);
                Uri uri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".fileprovider", result.pdfFile);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "Error during scan flow", e);
                new AlertDialog.Builder(this)
                        .setMessage("An error occurred: " + e.getMessage())
                        .show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
