package com.skateshare.viewmodels.feed

import android.app.Application
import android.app.Instrumentation
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import androidx.lifecycle.*
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.skateshare.misc.ExceptionResponse
import com.skateshare.misc.postToHashMap
import com.skateshare.repostitories.FirestorePost
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import javax.inject.Inject

class CreatePostViewModel(application: Application) : AndroidViewModel(application) {

    private val _exceptionResponse = MutableLiveData<ExceptionResponse>()
    val exceptionResponse: LiveData<ExceptionResponse> = _exceptionResponse

    fun pushPost(uri: Uri, description: String) {
        val uid = FirebaseAuth.getInstance().uid!!

        viewModelScope.launch(Dispatchers.IO) {
            try {
                FirestorePost.createPost(uri,
                    postToHashMap(description, uid)
                )
                _exceptionResponse.postValue(ExceptionResponse(
                    message = null,
                    isSuccessful = true))
            } catch(e: Exception) {
                _exceptionResponse.postValue(ExceptionResponse(
                    message = e.toString(),
                    isSuccessful = false))
            }
        }
    }

    fun resetException() {
        _exceptionResponse.postValue(ExceptionResponse(
            message = null,
            isSuccessful = false,
            isEnabled = false
        ))
    }
}
