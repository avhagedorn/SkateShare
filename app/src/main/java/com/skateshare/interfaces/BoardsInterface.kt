package com.skateshare.interfaces

import android.net.Uri
import com.google.firebase.firestore.DocumentSnapshot

interface BoardsInterface {

    suspend fun getBoard(id: String) : DocumentSnapshot
    suspend fun editBoard(id: String, uri: Uri?, boardData: HashMap<String, Any?>)
    suspend fun deleteBoard(id: String)

}