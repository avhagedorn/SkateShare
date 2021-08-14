package com.skateshare.interfaces

import android.net.Uri
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import java.util.HashMap

interface PostInterface {

    suspend fun getPosts(end: Timestamp) : QuerySnapshot
    suspend fun getPost(postId: String) : DocumentSnapshot
    suspend fun createPost(uri: Uri, postData: HashMap<String, Any?>)
    suspend fun deletePost(id: String)

}