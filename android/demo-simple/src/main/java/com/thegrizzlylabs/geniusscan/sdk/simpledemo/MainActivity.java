package com.thegrizzlylabs.geniusscan.sdk.simpledemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.thegrizzlylabs.geniusscan.sdk.ui.GeniusScanSdkUI;
import com.thegrizzlylabs.geniusscan.sdk.ui.ScanConfiguration;
import com.thegrizzlylabs.geniusscan.sdk.ui.ScanResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumSet;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button scanButton = findViewById(R.id.scan_button);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startScanning();
            }
        });
    }

    private void startScanning() {
        initSDK();

        ScanConfiguration scanConfiguration = new ScanConfiguration();

        boolean scanFromFile = false;
        if (scanFromFile) {
            scanConfiguration.source = ScanConfiguration.Source.IMAGE;
            scanConfiguration.sourceImage = new File(getExternalCacheDir(), "temp.jpg");
            try {
                copyFileFromResource(R.raw.scan, scanConfiguration.sourceImage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        scanConfiguration.multiPage = true;
        scanConfiguration.pdfPageSize = ScanConfiguration.PdfPageSize.FIT;
        scanConfiguration.pdfMaxScanDimension = 2000;
        scanConfiguration.jpegQuality = 60;
        scanConfiguration.postProcessingActions = EnumSet.of(ScanConfiguration.Action.EDIT_FILTER, ScanConfiguration.Action.ROTATE);
        scanConfiguration.flashButtonHidden = false;
        scanConfiguration.defaultFlashMode = ScanConfiguration.FlashMode.AUTO;
        scanConfiguration.backgroundColor = Color.WHITE;
        scanConfiguration.foregroundColor = ContextCompat.getColor(this, R.color.colorPrimary);
        scanConfiguration.highlightColor = ContextCompat.getColor(this, R.color.colorAccent);

        GeniusScanSdkUI.scanWithConfiguration(MainActivity.this, scanConfiguration);
    }

    private void initSDK() {
        ////////////////////////////////////////////////////////////////////////////////////////////////////
        // This code shows how to initialize the SDK with a license key.
        // Without a license key, the SDK runs for 60 seconds and then the app needs to be restarted.
        //
        // try {
        //    // Replace this key by your key
        //    GeniusScanSdkUI.init(this, "<Your license key>");
        // } catch(LicenseException e) {
        //    new AlertDialog.Builder(this)
        //            .setMessage("This version is not valid anymore. Please update to the latest version.")
        //            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
        //                @Override
        //                public void onClick(DialogInterface dialog, int which) {
        //                    finish();
        //                }
        //            })
        //            .show();
        // }
        ////////////////////////////////////////////////////////////////////////////////////////////////////
    }

    private void copyFileFromResource(@RawRes int fileResId, File destinationFile) throws IOException {
        InputStream in = getResources().openRawResource(fileResId);
        FileOutputStream out = new FileOutputStream(destinationFile);
        byte[] buff = new byte[1024];
        int read;

        try {
            while ((read = in.read(buff)) > 0) {
                out.write(buff, 0, read);
            }
        } finally {
            in.close();
            out.close();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == GeniusScanSdkUI.SCAN_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            try {
                ScanResult result = GeniusScanSdkUI.getScanResultFromActivityResult(data);
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
        }
    }
}
