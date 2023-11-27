package com.geniusscansdk.demo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.geniusscansdk.core.GeniusScanSDK;
import com.geniusscansdk.demo.camera.ScanActivity;
import com.geniusscansdk.demo.enhance.PdfGenerationTask;
import com.geniusscansdk.demo.model.DocumentManager;
import com.geniusscansdk.demo.model.Page;

import java.io.File;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.FileProvider;

/**
 * Created by guillaume on 29/09/16.
 */

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView pageCountView;
    private Button shareButton;
    private SwitchCompat ocrSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        getSupportActionBar().setTitle("GS SDK Custom Demo");

        // This code shows how to initialize the SDK with a license key.
        // Without a license key, the SDK runs for 60 seconds and then the app needs to be restarted.
        //
        // GeniusScanSDK.setLicenseKey(this, "<Your license key>");

        pageCountView = findViewById(R.id.page_count);

        shareButton = findViewById(R.id.share_button);
        shareButton.setOnClickListener(v -> shareDocument());

        ocrSwitch = findViewById(R.id.ocr_switch);

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

    private void shareDocument() {
        List<Page> pages = DocumentManager.getInstance(this).getPages();
        final File outputFile = new File(getExternalCacheDir(), "test.pdf");
        new PdfGenerationTask(this, pages, outputFile, ocrSwitch.isChecked(), (isSuccess, error) -> {
            if (!isSuccess) {
                Log.e(TAG, "Error generating PDF", error);
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                return;
            }

            // View generated PDF document with another compatible installed app
            Uri uri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".fileprovider", outputFile);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        }).execute();
    }
}
