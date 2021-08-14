package com.skateshare.repostitories

import android.net.Uri
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.skateshare.interfaces.PostInterface
import com.skateshare.misc.QUERY_LIMIT
import com.skateshare.models.User
import com.skateshare.models.User.Companion.newDefaultUser
import com.skateshare.models.User.Companion.toUser
import kotlinx.coroutines.tasks.await
import java.util.*

object FirestorePost : PostInterface {

    override suspend fun getPosts(end: Timestamp) : QuerySnapshot {
        return FirebaseFirestore.getInstance()
            .collection("posts")
            .whereLessThan("datePosted", end)
            .orderBy("datePosted")
            .limitToLast(QUERY_LIMIT.toLong())
            .get()
            .await()
    }

    override suspend fun getPost(postId: String) : DocumentSnapshot {
        return FirebaseFirestore.getInstance()
            .document("posts/$postId")
            .get()
            .await()
    }

    override suspend fun createPost(uri: Uri, postData: HashMap<String, Any?>) {
        val postId = postData["id"].toString()
        val imageReference = FirebaseStorage.getInstance()
            .getReference("postPictures/$postId")
        imageReference.putFile(uri).await()
        imageReference.downloadUrl.addOnSuccessListener { newUri ->
            postData["imageUrl"] = newUri.toString()

            FirebaseFirestore.getInstance()
                .document("posts/$postId").set(postData)
        }.await()
    }

    override suspend fun deletePost(id: String) {
        FirebaseFirestore.getInstance().document("posts/$id").delete()
        FirebaseStorage.getInstance().getReference("postPictures/$id").delete()
    }
}