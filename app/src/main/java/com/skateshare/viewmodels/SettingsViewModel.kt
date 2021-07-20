package com.skateshare.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.skateshare.repostitories.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// class SettingsViewModel @Inject constructor(repo: AuthRepository): ViewModel() {

class SettingsViewModel : ViewModel() {

    // private var repository: AuthRepository = repo

    fun logout() { AuthRepository().logout() }

    fun deleteAccount() {
        viewModelScope.launch(Dispatchers.IO) {
            AuthRepository().deleteAccount()
        }
    }
}