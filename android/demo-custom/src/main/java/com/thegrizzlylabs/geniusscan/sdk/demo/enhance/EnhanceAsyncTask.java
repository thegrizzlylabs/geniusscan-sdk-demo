package com.thegrizzlylabs.geniusscan.sdk.demo.enhance;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.thegrizzlylabs.geniusscan.sdk.core.LicenseException;
import com.thegrizzlylabs.geniusscan.sdk.core.ScanContainer;
import com.thegrizzlylabs.geniusscan.sdk.demo.processing.ScanProcessor;

class EnhanceAsyncTask extends AsyncTask<ScanContainer, Void, Void> {

   private final Context context;
   private Exception error = null;

   EnhanceAsyncTask(Context context) {
      this.context = context;
   }

   @Override
   protected Void doInBackground(ScanContainer... params) {
      try {
         ScanContainer scanContainer = params[0];
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