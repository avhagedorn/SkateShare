package com.skateshare.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skateshare.misc.ExceptionResponse
import com.skateshare.repostitories.FirestoreBugReport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BugReportViewModel : ViewModel() {

    private val _reportResponse = MutableLiveData<ExceptionResponse>()
    val reportResponse: LiveData<ExceptionResponse> get() = _reportResponse

    fun submitBugReport(bugLocation: String, bugDescription: String, uri: Uri?) {
        if (reportIsValid(bugLocation, bugDescription)) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    FirestoreBugReport.submitBugReport(bugLocation, bugDescription, uri)
                    _reportResponse.postValue(ExceptionResponse(
                        "Thank you, your report has been recorded.", true))
                } catch (e: Exception) {
                    _reportResponse.postValue(ExceptionResponse(e.message, false))
                }
            }
        } else {
            _reportResponse.postValue(ExceptionResponse(
                "Please fill out all required fields!", false))
        }
    }

    private fun reportIsValid(bugLocation: String, bugDescription: String) =
        bugLocation.trim{it<=' '}.isNotEmpty()
        && bugDescription.trim{it<=' '}.isNotEmpty()

    fun resetReportResponse() {
        _reportResponse.postValue(ExceptionResponse(null, false))
    }
}