package com.skateshare.interfaces

import android.net.Uri
import java.util.HashMap

interface BugReportInterface {

    suspend fun submitBugReport(bugLocation: String, bugDescription: String, uri: Uri?)
    fun saveReport(report: HashMap<String, Any?>)

}