package com.skateshare.repostitories

import android.net.Uri
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.skateshare.models.Post
import com.skateshare.models.Post.Companion.toPost
import kotlinx.coroutines.tasks.await
import org.w3c.dom.Document
import java.util.*

// TODO: TEMPORARY
object DummyPostRepository {

    suspend fun getPosts(end: Timestamp) : QuerySnapshot {
        return FirebaseFirestore.getInstance()
            .collection("posts")
            .whereLessThan("datePosted", end)
            .orderBy("datePosted")
            .limitToLast(10)
            .get()
            .await()
    }

    suspend fun createPost(uri: Uri, postData: HashMap<String, Any?>) {
        val postId = postData["id"].toString()
        val imageReference = FirebaseStorage.getInstance()
            .getReference("postPictures/$postId")
        imageReference.putFile(uri).addOnSuccessListener {
            imageReference.downloadUrl.addOnSuccessListener { newUri ->
                postData["imageUrl"] = newUri.toString()

                FirebaseFirestore.getInstance()
                    .document("posts/$postId").set(postData)
            }
        }
    }

    suspend fun deletePost(id: String) {
        FirebaseFirestore.getInstance().document("posts/$id").delete()
        FirebaseStorage.getInstance().getReference("postPictures/$id").delete()
    }
}