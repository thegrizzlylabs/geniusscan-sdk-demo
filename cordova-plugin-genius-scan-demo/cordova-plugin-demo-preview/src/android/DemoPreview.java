package com.geniusscansdk.cordova.demo.preview;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import androidx.core.content.FileProvider;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.Locale;

public class DemoPreview extends CordovaPlugin {
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (!"previewPath".equals(action)) {
            return false;
        }

        String path = args.getString(0);
        String mimeType = args.optString(2, "");
        previewPath(path, mimeType, callbackContext);
        return true;
    }

    private void previewPath(String path, String mimeType, CallbackContext callbackContext) {
        Uri uri = Uri.parse(path);
        if (uri == null) {
            callbackContext.error("Invalid file URL.");
            return;
        }

        File file;
        if ("file".equals(uri.getScheme())) {
            file = new File(uri.getPath());
        } else if (uri.getScheme() == null || uri.getScheme().isEmpty()) {
            file = new File(path);
        } else {
            callbackContext.error("Unsupported file URL.");
            return;
        }

        if (!file.exists()) {
            callbackContext.error("File not found.");
            return;
        }

        final File previewFile = file;
        final String resolvedMimeType = mimeType == null || mimeType.isEmpty() ? mimeTypeForFile(file) : mimeType;

        cordova.getActivity().runOnUiThread(() -> {
            try {
                Uri contentUri = FileProvider.getUriForFile(
                    cordova.getActivity(),
                    cordova.getActivity().getPackageName() + ".previewfileprovider",
                    previewFile
                );

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(contentUri, resolvedMimeType);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                cordova.getActivity().startActivity(Intent.createChooser(intent, "Open file"));
                callbackContext.success("opened");
            } catch (ActivityNotFoundException exception) {
                callbackContext.error("No application found to open this file.");
            } catch (IllegalArgumentException exception) {
                callbackContext.error("Unable to share this file.");
            }
        });
    }

    private String mimeTypeForFile(File file) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(file.getName());
        if (extension == null || extension.isEmpty()) {
            return "application/pdf";
        }

        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase(Locale.US));
        return mimeType == null || mimeType.isEmpty() ? "application/pdf" : mimeType;
    }
}
