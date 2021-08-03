package com.skateshare.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skateshare.repostitories.AuthenticationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {

    fun logout() { AuthenticationService.logout() }

    fun deleteAccount() {
        viewModelScope.launch(Dispatchers.IO) {
            AuthenticationService.deleteAccount()
        }
    }
}