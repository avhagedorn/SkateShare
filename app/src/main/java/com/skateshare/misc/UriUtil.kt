package com.skateshare.misc

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile

fun Uri.fileSizeBytes(context: Context) : Long {
    return DocumentFile.fromSingleUri(context, this)?.length() ?: 0L
}

fun Uri.fileSizeMb(context: Context) : Double {
    val bytes = fileSizeBytes(context)
    return bytes.toDouble() / 1000000.0
}