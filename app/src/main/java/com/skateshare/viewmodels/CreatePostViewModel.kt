package com.skateshare.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.skateshare.repostitories.DummyPostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class CreatePostViewModel : ViewModel() {

    fun pushPost(uri: Uri, description: String) {
        val uid = FirebaseAuth.getInstance().uid!!

        viewModelScope.launch(Dispatchers.IO) {
            DummyPostRepository.createPost(uri,
                hashMapOf<String, Any?>(
                    "id" to UUID.randomUUID().toString(),
                    "description" to description,
                    "postedBy" to uid,
                    "datePosted" to Timestamp.now()
                )
            )
        }
    }
}
