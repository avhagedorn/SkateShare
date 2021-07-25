package com.skateshare.repostitories

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.skateshare.models.Post
import com.skateshare.models.Post.Companion.toPost
import kotlinx.coroutines.tasks.await

object DummyPostRepository {

    suspend fun getPosts(): List<Post?> {
        val mutablePosts: MutableList<Post?> = mutableListOf()
        val query = FirebaseFirestore.getInstance()
            .collection("posts")
            .get()
            .await()
        for (item in query) {
            mutablePosts.add(item.toPost())
        }
        return mutablePosts
    }
}