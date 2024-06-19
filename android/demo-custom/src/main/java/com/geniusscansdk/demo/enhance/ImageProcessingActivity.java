package com.geniusscansdk.demo.enhance;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.geniusscansdk.core.RotationAngle;
import com.geniusscansdk.demo.MainActivity;
import com.geniusscansdk.demo.R;
import com.geniusscansdk.demo.model.DocumentManager;
import com.geniusscansdk.demo.model.Page;

import java.io.File;

public class ImageProcessingActivity extends AppCompatActivity {

   @SuppressWarnings("unused")
   private final static String TAG = ImageProcessingActivity.class.getSimpleName();

   public final static String EXTRA_PAGE = "page";

   private ImageView imageView;
   private ImageView distortionCorrectionButton;
   private ProgressDialog progressDialog;
   private Page page;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      page = getIntent().getParcelableExtra(EXTRA_PAGE);

      setContentView(R.layout.image_processing_activity);
      imageView = findViewById(R.id.image_view);
      distortionCorrectionButton = findViewById(R.id.distortion_correction_button);
   }

   @Override
   protected void onResume() {
      super.onResume();
      updateDistortionCorrectionButton();
      displayScan(page.getOriginalImage());

      progressDialog = new ProgressDialog(this);
      progressDialog.show();
      enhance();
   }

   private void updateDistortionCorrectionButton() {
      distortionCorrectionButton.setImageResource(page.isDistortionCorrectionEnabled() ? R.drawable.straightened_distortion_grid : R.drawable.distortion_grid);
   }

   private void endEnhance() {
      displayEnhancedScan();
      progressDialog.dismiss();
   }

   private void endRotate() {
      displayEnhancedScan();
      progressDialog.dismiss();
   }

   public void changeEnhancement(View view) {
      new AlertDialog.Builder(this)
              .setTitle(R.string.enhancement_dialog_title)
              .setItems(new CharSequence[]{
                      getString(R.string.image_type_none),
                      getString(R.string.image_type_auto),
                      getString(R.string.image_type_black_white),
                      getString(R.string.image_type_color),
                      getString(R.string.image_type_photo)
              }, (dialog, which) -> {
                  Filter filterType = Filter.values()[which];
                  page.setFilter(filterType);
                  progressDialog.show();
                  enhance();
              }).show();
   }

   public void toggleDistortionCorrection(View view) {
      page.setDistortionCorrectionEnabled(!page.isDistortionCorrectionEnabled());
      updateDistortionCorrectionButton();
      progressDialog.show();
      enhance();
   }

   private void rotate(RotationAngle angle) {
      progressDialog.show();
      new RotateAsyncTask(this, angle) {
         @Override
         protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            endRotate();
         }
      }.execute(page);
   }

   private void enhance() {
      new EnhanceAsyncTask(this) {
         @Override
         protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            endEnhance();
         }
      }.execute(page);
   }

   private void displayEnhancedScan() {
      displayScan(page.getEnhancedImage());
   }

   private void displayScan(File imageFile) {
      Options opts = new Options();
      opts.inSampleSize = 2;
      Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), opts);
      imageView.setImageBitmap(bitmap);
      imageView.invalidate();
   }

   public void rotateLeft(View view) {
      rotate(RotationAngle.ROTATION_90_CCW);
   }

   public void rotateRight(View view) {
      rotate(RotationAngle.ROTATION_90_CW);
   }

   public void savePage(View view) {
      DocumentManager.getInstance(this).addPage(page);
      Intent intent = new Intent(this, MainActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      startActivity(intent);
      finish();
   }

}
