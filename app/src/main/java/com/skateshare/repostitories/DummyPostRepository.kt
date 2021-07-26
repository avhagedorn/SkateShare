package com.skateshare.repostitories

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.skateshare.models.Post
import com.skateshare.models.Post.Companion.toPost
import kotlinx.coroutines.tasks.await
import java.util.*

// TODO: TEMPORARY
object DummyPostRepository {

    suspend fun getPosts(): List<Post?> {
        val mutablePosts: MutableList<Post?> = mutableListOf()
        val query = FirebaseFirestore.getInstance()
                    .collection("posts")
                    .limit(10)
                    .orderBy("datePosted")
                    .get()
                    .await()

        query.forEach { item ->
            val post = item.toPost()
            if (post != null)
                mutablePosts.add(post)
        }
        return mutablePosts
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

    suspend fun getUserReference(uid: String) : DocumentReference? {
        return try {
            FirebaseFirestore.getInstance().document("users/$uid")
        } catch (e: Exception) {
            Log.e("DummyPostRepository", e.toString())
            null
        }
    }
}