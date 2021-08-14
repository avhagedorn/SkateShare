package com.skateshare.interfaces

import android.net.Uri
import com.skateshare.models.User

interface UserInterface {

    suspend fun getUserData(uid: String) : User
    suspend fun setUserData(user: Map<String, Any?>)
    suspend fun deleteUserData(uid: String)
    suspend fun updateUserData(user: Map<String, Any?>, uid: String)
    suspend fun uploadProfilePicture(uid: String, uri: Uri)

}