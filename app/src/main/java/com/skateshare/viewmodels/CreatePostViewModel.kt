package com.skateshare.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.skateshare.misc.ExceptionResponse
import com.skateshare.repostitories.FirestorePost
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class CreatePostViewModel : ViewModel() {

    private val _exceptionResponse = MutableLiveData<ExceptionResponse>()
    val exceptionResponse: LiveData<ExceptionResponse> = _exceptionResponse

    fun pushPost(uri: Uri, description: String) {
        val uid = FirebaseAuth.getInstance().uid!!

        viewModelScope.launch(Dispatchers.IO) {
            try {
                FirestorePost.createPost(uri,
                    hashMapOf<String, Any?>(
                        "id" to UUID.randomUUID().toString(),
                        "description" to description,
                        "postedBy" to uid,
                        "datePosted" to Timestamp.now()
                    )
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
