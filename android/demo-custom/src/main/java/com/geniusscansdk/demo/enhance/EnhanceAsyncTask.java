package com.geniusscansdk.demo.enhance;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.geniusscansdk.core.LicenseException;
import com.geniusscansdk.demo.model.Page;
import com.geniusscansdk.demo.processing.PageProcessor;

class EnhanceAsyncTask extends AsyncTask<Page, Void, Void> {

   private final Context context;
   private Exception error = null;

   EnhanceAsyncTask(Context context) {
      this.context = context;
   }

   @Override
   protected Void doInBackground(Page... params) {
      try {
         Page page = params[0];
         new PageProcessor().processPage(context, page);
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