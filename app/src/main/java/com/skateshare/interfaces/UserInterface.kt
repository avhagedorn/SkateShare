package com.skateshare.interfaces

import android.net.Uri
import com.google.firebase.firestore.DocumentSnapshot
import com.skateshare.models.User

interface UserInterface {

    suspend fun getUserData(uid: String) : DocumentSnapshot
    suspend fun setUserData(user: HashMap<String, Any?>)
    suspend fun deleteUserData(uid: String)
    suspend fun updateUserData(user: HashMap<String, Any?>, uid: String)
    suspend fun uploadProfilePicture(uid: String, uri: Uri)

}