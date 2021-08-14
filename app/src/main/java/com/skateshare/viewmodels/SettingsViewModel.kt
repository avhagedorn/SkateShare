package com.skateshare.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skateshare.repostitories.FirebaseAuthentication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {

    fun logout() { FirebaseAuthentication.logout() }

    fun deleteAccount() {
        viewModelScope.launch(Dispatchers.IO) {
            FirebaseAuthentication.deleteAccount()
        }
    }
}