package com.thegrizzlylabs.geniusscan.sdk.demo.processing;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.thegrizzlylabs.geniusscan.sdk.demo.R;
import com.thegrizzlylabs.geniusscan.sdk.demo.enhance.ImageProcessingActivity;
import com.thegrizzlylabs.geniusscan.sdk.demo.model.Page;
import com.thegrizzlylabs.geniusscan.sdk.ui.BorderDetectionImageView;
import com.thegrizzlylabs.geniusscan.sdk.ui.MagnifierBorderDetectionListener;
import com.thegrizzlylabs.geniusscan.sdk.ui.MagnifierView;

public class BorderDetectionActivity extends AppCompatActivity {

   @SuppressWarnings("unused")
   private static final String TAG = BorderDetectionActivity.class.getSimpleName();

   private Page page;

   private ProgressDialog progressDialog;
   private BorderDetectionImageView imageView;
   private MagnifierView magnifierView;

   public final static String EXTRA_PAGE = "page";

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      setContentView(R.layout.border_detection_activity);

      imageView = findViewById(R.id.image_view);
      imageView.setOverlayColorResource(R.color.blue);

      magnifierView = findViewById(R.id.magnifier_view);
      imageView.setListener(new MagnifierBorderDetectionListener(magnifierView));

      page = getIntent().getParcelableExtra(EXTRA_PAGE);
   }

   @Override
   protected void onResume() {
      super.onResume();
      String filename = page.getOriginalImage().getAbsolutePath(this);
      BitmapFactory.Options opts = new BitmapFactory.Options();
      opts.inSampleSize = 2;

      Bitmap bitmap = BitmapFactory.decodeFile(filename, opts);
      imageView.setImageBitmap(bitmap);
      magnifierView.setBitmap(bitmap);

      progressDialog = new ProgressDialog(this);

      progressDialog.show();
      new AnalyzeAsyncTask(this) {
         @Override
         protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            endAnalyze();
         }
      }.execute(page);
   }

   protected void endAnalyze() {
      progressDialog.dismiss();
      addQuadrangleToView();
   }

   void addQuadrangleToView() {
      imageView.setQuad(page.getQuadrangle());
      imageView.invalidate();
   }

   public void setQuadrangleToFullImage(View view) {
      page.getQuadrangle().setToFullImage();
      imageView.invalidate();
   }

   public void select(View view) {
      Intent intent = new Intent(this, ImageProcessingActivity.class);
      intent.putExtra(ImageProcessingActivity.EXTRA_PAGE, page);
      startActivity(intent);
   }

}
