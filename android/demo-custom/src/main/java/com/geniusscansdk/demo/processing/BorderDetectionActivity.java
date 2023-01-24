package com.geniusscansdk.demo.processing;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;

import com.geniusscansdk.core.Quadrangle;
import com.geniusscansdk.demo.R;
import com.geniusscansdk.demo.enhance.ImageProcessingActivity;
import com.geniusscansdk.demo.model.Page;
import com.geniusscansdk.ui.BorderDetectionImageView;
import com.geniusscansdk.ui.MagnifierBorderDetectionListener;
import com.geniusscansdk.ui.MagnifierView;

import androidx.appcompat.app.AppCompatActivity;

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
      String filename = page.getOriginalImage().getAbsolutePath();
      BitmapFactory.Options opts = new BitmapFactory.Options();
      opts.inSampleSize = 2;

      Bitmap bitmap = BitmapFactory.decodeFile(filename, opts);
      imageView.setImageBitmap(bitmap);
      magnifierView.setBitmap(bitmap);

      progressDialog = new ProgressDialog(this);

      progressDialog.show();
      new AnalyzeAsyncTask(this) {
         @Override
         protected void onPostExecute(Quadrangle quadrangle) {
            super.onPostExecute(quadrangle);
            progressDialog.dismiss();
            addQuadrangleToView(quadrangle);

         }
      }.execute(page);
   }

   void addQuadrangleToView(Quadrangle quadrangle) {
      imageView.setQuad(quadrangle);
      imageView.invalidate();
   }

   public void setQuadrangleToFullImage(View view) {
      imageView.setQuad(Quadrangle.createFullQuadrangle());
      imageView.invalidate();
   }

   public void select(View view) {
      page.setQuadrangle(imageView.getQuad());
      Intent intent = new Intent(this, ImageProcessingActivity.class);
      intent.putExtra(ImageProcessingActivity.EXTRA_PAGE, page);
      startActivity(intent);
   }

}
