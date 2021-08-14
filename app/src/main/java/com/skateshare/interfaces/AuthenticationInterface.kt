package com.skateshare.interfaces

interface AuthenticationInterface {

    suspend fun register(email: String, password: String)
    suspend fun login(email: String, password: String)
    fun logout()
    suspend fun deleteAccount()
}