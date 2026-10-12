package com.geniusscansdk.demo.util;

import android.app.AlertDialog;
import android.content.Context;

import androidx.annotation.NonNull;

public final class ErrorDisplayUtil {
    public static void displayError(
            @NonNull Context context,
            @NonNull Exception exception,
            @NonNull String defaultMessage
    ) {
        String message = exception.getMessage() != null
                ? exception.getMessage()
                : defaultMessage;
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }
}
