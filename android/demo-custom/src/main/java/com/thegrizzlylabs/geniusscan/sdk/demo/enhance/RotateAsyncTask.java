package com.thegrizzlylabs.geniusscan.sdk.demo.enhance;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.thegrizzlylabs.geniusscan.sdk.core.GeniusScanLibrary;
import com.thegrizzlylabs.geniusscan.sdk.core.LicenseException;
import com.thegrizzlylabs.geniusscan.sdk.core.RotationAngle;
import com.thegrizzlylabs.geniusscan.sdk.core.ScanContainer;
import com.thegrizzlylabs.geniusscan.sdk.demo.processing.ScanProcessor;

class RotateAsyncTask extends AsyncTask<ScanContainer, Void, Void> {

   private final Context context;
   private final RotationAngle angle;
   private Exception error = null;

   RotateAsyncTask(Context context, RotationAngle angle) {
      this.context = context;
      this.angle = angle;
   }

   @Override
   protected Void doInBackground(ScanContainer... params) {
      ScanContainer scanContainer = params[0];
      try {
         if (scanContainer.getQuadrangle() != null) {
            scanContainer.setQuadrangle(scanContainer.getQuadrangle().rotate(angle));
         }
         GeniusScanLibrary.rotateImage(scanContainer.getOriginalImage().getAbsolutePath(context), scanContainer.getOriginalImage().getAbsolutePath(context), angle);


         // original image was rotated, let's reprocess it
         new ScanProcessor().processPage(context, scanContainer);
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