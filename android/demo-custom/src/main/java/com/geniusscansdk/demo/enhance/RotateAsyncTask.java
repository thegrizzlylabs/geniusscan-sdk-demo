package com.geniusscansdk.demo.enhance;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.geniusscansdk.core.GeniusScanSDK;
import com.geniusscansdk.core.LicenseException;
import com.geniusscansdk.core.RotationAngle;
import com.geniusscansdk.demo.model.Page;
import com.geniusscansdk.demo.processing.PageProcessor;

class RotateAsyncTask extends AsyncTask<Page, Void, Void> {

   private final Context context;
   private final RotationAngle angle;
   private Exception error = null;

   RotateAsyncTask(Context context, RotationAngle angle) {
      this.context = context;
      this.angle = angle;
   }

   @Override
   protected Void doInBackground(Page... params) {
      Page scanContainer = params[0];
      try {
         if (scanContainer.getQuadrangle() != null) {
            scanContainer.setQuadrangle(scanContainer.getQuadrangle().rotate(angle));
         }
         String imagePath = scanContainer.getOriginalImage().getAbsolutePath();
         GeniusScanSDK.rotateImage(imagePath, imagePath, angle);


         // original image was rotated, let's reprocess it
         new PageProcessor().processPage(context, scanContainer);
      } catch (Exception e) {
         error = e;
      }

      return null;
   }

   @Override
   protected void onPostExecute(Void aVoid) {
      if (error instanceof LicenseException) {
         new AlertDialog.Builder(context)
                 .setMessage(error.getMessage())
                 .setPositiveButton(android.R.string.ok, null)
                 .show();
      }
   }
}
