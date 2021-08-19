package com.skateshare.repostitories

import android.net.Uri
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.skateshare.interfaces.BoardsInterface
import kotlinx.coroutines.tasks.await

object FirestoreBoards : BoardsInterface {

    override suspend fun getBoard(id: String) : DocumentSnapshot {
        return FirebaseFirestore.getInstance()
            .document("boards/$id")
            .get()
            .await()
    }

    override suspend fun editBoard(id: String, uri: Uri?, boardData: HashMap<String, Any?>) {
        if (uri != null) {
            val imageReference = FirebaseStorage.getInstance()
                .getReference("boards/$id")
            imageReference.putFile(uri).await()
            imageReference.downloadUrl.addOnSuccessListener { newUri ->
                boardData["imageUrl"] = newUri.toString()

                FirebaseFirestore.getInstance()
                    .document("boards/$id")
                    .set(boardData)
            }.await()
        }
        FirebaseFirestore.getInstance()
            .document("boards/$id")
            .set(boardData)
            .await()
    }

    override suspend fun deleteBoard(id: String) {
        FirebaseFirestore.getInstance()
            .document("boards/$id")
            .delete()
    }

}