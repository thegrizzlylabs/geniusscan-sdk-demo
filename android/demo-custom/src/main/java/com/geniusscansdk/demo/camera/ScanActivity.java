package com.geniusscansdk.demo.camera;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.geniusscansdk.camera.DetectionMode;
import com.geniusscansdk.camera.FileImageCaptureCallback;
import com.geniusscansdk.camera.FocusIndicator;
import com.geniusscansdk.camera.ScanFragment;
import com.geniusscansdk.camera.realtime.BorderDetector;
import com.geniusscansdk.core.QuadStreamAnalyzer;
import com.geniusscansdk.core.RotationAngle;
import com.geniusscansdk.demo.util.ErrorDisplayUtil;
import com.geniusscansdk.demo.R;
import com.geniusscansdk.demo.model.Page;
import com.geniusscansdk.demo.processing.BorderDetectionActivity;

import java.io.File;
import java.util.UUID;

public class ScanActivity extends AppCompatActivity implements ScanFragment.CameraCallbackProvider {
   private static final String TAG = ScanActivity.class.getSimpleName();
   private static final int PERMISSION_REQUEST_CODE = 1;

   private ScanFragment scanFragment;
   private TextView userGuidanceTextView;
   private final PageRotation pageRotation = new PageRotation();

   private boolean cameraPermissionGranted = false;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      EdgeToEdge.enable(this);
      setContentView(R.layout.scanning_activity);

      Button captureButton = findViewById(R.id.captureButton);
      captureButton.setOnClickListener(view -> takePicture());

      userGuidanceTextView = findViewById(R.id.user_guidance);

      FocusIndicator focusIndicator = findViewById(R.id.focus_indicator);

      scanFragment = new ScanFragment();
      getSupportFragmentManager().beginTransaction().replace(R.id.scan_fragment_layout, scanFragment).commit();

      scanFragment.setPreviewAspectFill(false);
      scanFragment.setDetectionMode(DetectionMode.Document.INSTANCE);
      scanFragment.setFocusIndicator(focusIndicator);
      scanFragment.setAutoTriggerAnimationEnabled(true);
      scanFragment.setBorderDetectorListener(new BorderDetector.BorderDetectorListener() {
         @Override
         public void onBorderDetectionResult(@NonNull QuadStreamAnalyzer.Result result) {
            if (result.status == QuadStreamAnalyzer.Status.TRIGGER) {
               takePicture();
            }
            updateUserGuidance(result);
         }

         @Override
         public void onBorderDetectionFailure(@NonNull Exception e) {
            scanFragment.setPreviewEnabled(false);
            new AlertDialog.Builder(ScanActivity.this)
                    .setMessage(e.getMessage())
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> finish())
                    .show();
         }
      });

      cameraPermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
      if (!cameraPermissionGranted) {
         ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
      }
   }

   @Override
   protected void onResume() {
      super.onResume();

      if (cameraPermissionGranted) {
         scanFragment.initializeCamera();
      }
   }

   @Override
   protected void onDestroy() {
      pageRotation.close();
      super.onDestroy();
   }

   @Override
   public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
      if (requestCode == PERMISSION_REQUEST_CODE) {
         cameraPermissionGranted =  grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
         // Camera will be initialized in onResume
      } else {
         super.onRequestPermissionsResult(requestCode, permissions, grantResults);
      }
   }

   private void takePicture() {
      File outputFile = new File(getExternalFilesDir(null), UUID.randomUUID().toString() + ".jpeg");
      scanFragment.takePicture(new FileImageCaptureCallback(outputFile) {
         @Override
         public void onImageCaptured() {
            Page page = new Page(outputFile);
            rotatePage(page, RotationAngle.ROTATION_0);
         }

         @Override
         public void onError(@NonNull Exception e) {
            Toast.makeText(ScanActivity.this, "Capture failed", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Capture failed", e);
         }
      });
   }

   private void rotatePage(Page page, RotationAngle rotationAngle) {
      ProgressDialog progressDialog = new ProgressDialog(this);
      progressDialog.show();

      pageRotation.rotate(page, rotationAngle, new PageRotation.Callback() {
         @Override
         public void onSuccess() {
            runOnUiThread(() -> {
               progressDialog.dismiss();
               launchBorderDetection(page);
            });
         }

         @Override
         public void onFailure(@NonNull Exception exception) {
            runOnUiThread(() -> {
               progressDialog.dismiss();
               ErrorDisplayUtil.displayError(
                       ScanActivity.this,
                       exception,
                       "Unable to rotate image"
               );
            });
         }
      });
   }

   private void launchBorderDetection(Page page) {
      Intent intent = new Intent(this, BorderDetectionActivity.class);
      intent.putExtra(BorderDetectionActivity.EXTRA_PAGE, page);
      startActivity(intent);
   }

   private void updateUserGuidance(QuadStreamAnalyzer.Result result) {
      int textResId = getUserGuidanceResId(result);
      if (textResId == 0) {
         userGuidanceTextView.setVisibility(View.INVISIBLE);
      } else {
         userGuidanceTextView.setVisibility(View.VISIBLE);
         userGuidanceTextView.setText(textResId);
      }
   }

   private int getUserGuidanceResId(QuadStreamAnalyzer.Result result) {
      if (result.status == QuadStreamAnalyzer.Status.NOT_FOUND || result.resultQuadrangle == null) {
         return R.string.detection_status_searching;
      } else if (result.status == QuadStreamAnalyzer.Status.SEARCHING || result.status == QuadStreamAnalyzer.Status.ABOUT_TO_TRIGGER) {
         return R.string.detection_status_found;
      }
      return 0;
   }

   @NonNull
   @Override
   public ScanFragment.Callback getCameraCallback() {
      return new ScanFragment.Callback() {
         @Override
         public void onCameraFailure() {
            Toast.makeText(ScanActivity.this, "Camera error", Toast.LENGTH_SHORT).show();
            finish();
         }
      };
   }

}
