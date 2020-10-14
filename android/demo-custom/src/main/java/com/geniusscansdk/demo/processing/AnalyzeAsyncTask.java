package com.geniusscansdk.demo.processing;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.geniusscansdk.core.CnnDocumentDetection;
import com.geniusscansdk.core.DocumentDetection;
import com.geniusscansdk.core.LicenseException;
import com.geniusscansdk.core.Quadrangle;
import com.geniusscansdk.demo.model.Page;

import java.io.File;

class AnalyzeAsyncTask extends AsyncTask<Page, Void, Quadrangle> {

   private final Context context;
   private Exception error;
   private DocumentDetection documentDetection;

   AnalyzeAsyncTask(Context context) {
      this.context = context;
      documentDetection = new CnnDocumentDetection(context);
   }

   @Override
   protected Quadrangle doInBackground(Page... params) {
      try {
         Page scanContainer = params[0];
         File imageFile = new File(scanContainer.getOriginalImage().getAbsolutePath(context));
         return documentDetection.detectDocument(imageFile);
      } catch (Exception e) {
         error = e;
         return null;
      }
   }

   @Override
   protected void onPostExecute(Quadrangle quadrangle) {
      if (error instanceof LicenseException) {
         new AlertDialog.Builder(context)
                 .setMessage(error.getMessage())
                 .setPositiveButton(android.R.string.ok, null)
                 .show();
      }
   }
}