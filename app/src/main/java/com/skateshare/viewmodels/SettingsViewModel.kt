package com.skateshare.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skateshare.repostitories.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {

    // private var repository: AuthRepository = repo

    fun logout() { AuthRepository().logout() }

    fun deleteAccount() {
        viewModelScope.launch(Dispatchers.IO) {
            AuthRepository().deleteAccount()
        }
    }
}