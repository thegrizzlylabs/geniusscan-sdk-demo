package com.geniusscansdk.demo.model

import android.content.Context
import androidx.compose.runtime.mutableStateListOf

class DocumentPageState private constructor() {
    val pages = mutableStateListOf<Page>()
    var currentPage: Page? = null

    fun addPage(page: Page) {
        pages += page
    }

    companion object {
        private var instance: DocumentPageState? = null

        fun getInstance(context: Context): DocumentPageState = instance ?: synchronized(this) {
            instance ?: DocumentPageState().also {
                context.getExternalFilesDir(null)?.listFiles()?.forEach { file -> file.delete() }
                instance = it
            }
        }
    }
}
