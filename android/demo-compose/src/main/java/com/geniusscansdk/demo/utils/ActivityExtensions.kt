package com.geniusscansdk.demo.utils

import android.app.Activity
import android.content.Intent
import com.geniusscansdk.demo.MainActivity

internal fun Activity.restartApp() {
    val intent = Intent(this, MainActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
    finish()

    Runtime.getRuntime().exit(0)
}