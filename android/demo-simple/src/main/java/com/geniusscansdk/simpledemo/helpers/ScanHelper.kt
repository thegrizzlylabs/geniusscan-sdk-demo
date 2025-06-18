package com.geniusscansdk.simpledemo.helpers

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import com.geniusscansdk.core.LicenseException
import com.geniusscansdk.scanflow.ScanConfiguration
import com.geniusscansdk.scanflow.ScanFlow.getScanResultFromActivityResult
import com.geniusscansdk.scanflow.ScanResult
import com.geniusscansdk.simpledemo.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object ScanHelper {

    fun createBaseOcrConfiguration(): ScanConfiguration.OcrConfiguration {
        return ScanConfiguration.OcrConfiguration().apply {
            languages = listOf("en-US")
        }
    }

    fun getScanResult(intent: Intent, activity: Activity): ScanResult? {
        try {
            return getScanResultFromActivityResult(intent)
        } catch (e: LicenseException) {
            if (e.errorCode == LicenseException.ErrorCode.ExpiredDemo) {
                MaterialAlertDialogBuilder(activity)
                    .setMessage(e.message)
                    .setPositiveButton("Restart") { _: DialogInterface?, _: Int -> restartApp(activity) }
                    .show()
            } else {
                // The license key is invalid or expired, either ask the user to update the app or provide a fallback
                MaterialAlertDialogBuilder(activity)
                    .setMessage("Please update to the latest version.")
                    .setPositiveButton(android.R.string.ok) { _: DialogInterface?, _: Int -> }
                    .show()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error during scan flow", e)
            MaterialAlertDialogBuilder(activity)
                .setMessage("An error occurred: " + e.message)
                .setPositiveButton(android.R.string.ok) { _: DialogInterface?, _: Int -> }
                .show()
        }

        return null
    }

    private fun restartApp(activity: Activity) {
        val intent = Intent(activity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activity.startActivity(intent)
        activity.finish()

        Runtime.getRuntime().exit(0)
    }

    private val TAG: String = ScanHelper::class.java.simpleName
}
