package com.geniusscansdk.demo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.geniusscansdk.demo.camera.ScanActivity;
import com.geniusscansdk.demo.enhance.PdfGenerationTask;
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
    private Switch ocrSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        initSDK();

        pageCountView = findViewById(R.id.page_count);

        shareButton = findViewById(R.id.share_button);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareDocument();
            }
        });

        ocrSwitch = findViewById(R.id.ocr_switch);

        Button cameraButton = findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ScanActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        int pageCount = DocumentManager.getInstance(this).getPages().size();
        pageCountView.setText(getResources().getQuantityString(R.plurals.page_count, pageCount, pageCount));
        shareButton.setEnabled(pageCount > 0);
        ocrSwitch.setEnabled(pageCount > 0);
    }

    private void initSDK() {
        ////////////////////////////////////////////////////////////////////////////////////////////////////
        // This code shows how to initialize the SDK with a license key.
        // Without a license key, the SDK runs for 60 seconds and then the app needs to be restarted.
        //
        //         try {
        //            // Replace this key by your key
        //            GeniusScanSDK.init(this, "<Your license key>");
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
    }

    private void shareDocument() {
        List<Page> pages = DocumentManager.getInstance(this).getPages();
        final File outputFile = new File(getExternalCacheDir(), "test.pdf");
        new PdfGenerationTask(this, pages, outputFile.getAbsolutePath(), ocrSwitch.isChecked(), new PdfGenerationTask.OnPdfGeneratedListener() {
            @Override
            public void onPdfGenerated(boolean isSuccess, Exception error) {
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
            }
        }).execute();
    }
}
