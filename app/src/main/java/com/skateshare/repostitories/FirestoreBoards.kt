package com.skateshare.repostitories

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.skateshare.interfaces.BoardsInterface
import kotlinx.coroutines.tasks.await

object FirestoreBoards : BoardsInterface {

    override suspend fun getBoard(id: String) : DocumentSnapshot {
        return FirebaseFirestore.getInstance()
            .document("boards/$id")
            .get()
            .await()
    }

    override suspend fun createBoard(id: String, boardData: HashMap<String, Any?>) {
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