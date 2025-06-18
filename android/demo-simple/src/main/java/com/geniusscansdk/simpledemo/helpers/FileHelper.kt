package com.geniusscansdk.simpledemo.helpers

import android.content.res.Resources
import androidx.annotation.RawRes
import java.io.File
import java.io.FileOutputStream

object FileHelper {

    fun copyFileFromResource(@RawRes fileResId: Int, destinationFile: File, resources: Resources) {
        if (destinationFile.exists()) {
            return
        }

        resources.openRawResource(fileResId).use { input ->
            FileOutputStream(destinationFile).use { output ->
                input.copyTo(output)
            }
        }
    }
}
