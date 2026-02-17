package com.geniusscansdk.simpledemo.helpers

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import com.geniusscansdk.core.LicenseException
import com.geniusscansdk.scanflow.FlowOutput
import com.geniusscansdk.scanflow.ScanFlowConfiguration
import com.geniusscansdk.scanflow.ScanFlowErrorCode
import com.geniusscansdk.scanflow.ScanFlowResult
import com.geniusscansdk.simpledemo.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object ScanHelper {

    fun createBaseOcrConfiguration(): ScanFlowConfiguration.OcrConfiguration {
        return ScanFlowConfiguration.OcrConfiguration().apply {
            languages = listOf("en-US")
        }
    }

    fun getScanResult(flowOutput: FlowOutput<ScanFlowResult>, activity: Activity): ScanFlowResult? {
        if (flowOutput is FlowOutput.Success) {
            return flowOutput.result
        }
        val error = (flowOutput as FlowOutput.Error).error
        when (error.code) {
            ScanFlowErrorCode.CANCELLATION -> {
                // Don't show any error
            }
            ScanFlowErrorCode.LICENSING -> {
                if ((error.underlyingError as? LicenseException)?.errorCode == LicenseException.ErrorCode.ExpiredDemo) {
                    MaterialAlertDialogBuilder(activity)
                        .setMessage(error.message)
                        .setPositiveButton("Restart") { _: DialogInterface?, _: Int -> restartApp(activity) }
                        .show()
                } else {
                    // The license key is invalid or expired, either ask the user to update the app or provide a fallback
                    MaterialAlertDialogBuilder(activity)
                        .setMessage("Please update to the latest version.")
                        .setPositiveButton(android.R.string.ok) { _: DialogInterface?, _: Int -> }
                        .show()
                }
            }
            else -> {
                Log.e(TAG, "Error during scan flow", error)
                MaterialAlertDialogBuilder(activity)
                    .setMessage("An error occurred: " + error.message)
                    .setPositiveButton(android.R.string.ok) { _: DialogInterface?, _: Int -> }
                    .show()
            }
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
