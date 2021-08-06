package com.skateshare.repostitories

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.skateshare.misc.reportToHashMap
import kotlinx.coroutines.tasks.await
import java.util.*

object FirestoreBugReport {

    suspend fun submitBugReport(bugLocation: String, bugDescription: String, uri: Uri?) {
        val uid = FirebaseAuth.getInstance().uid!!
        val reportId = UUID.randomUUID().toString()
        val reportData = reportToHashMap(bugLocation, bugDescription, uid)

        if (uri != null) {
            val screenshot = FirebaseStorage.getInstance()
                .getReference("bugReports/$reportId")
            screenshot.putFile(uri).await()
            screenshot.downloadUrl.addOnSuccessListener { newUri ->
                reportData["bugScreenshot"] = newUri.toString()

                saveReport(reportData)
            }
        } else {
            saveReport(reportData)
        }
    }

    private fun saveReport(report: HashMap<String, Any?>) {
        FirebaseFirestore.getInstance()
            .document("bugReports/${UUID.randomUUID()}")
            .set(report)
    }
}