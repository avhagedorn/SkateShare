package com.skateshare.interfaces

import com.google.firebase.firestore.DocumentSnapshot

interface BoardsInterface {

    suspend fun getBoard(id: String) : DocumentSnapshot
    suspend fun createBoard(id: String, boardData: HashMap<String, Any?>)
    suspend fun deleteBoard(id: String)

}