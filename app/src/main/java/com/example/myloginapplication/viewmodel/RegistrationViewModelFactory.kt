package com.example.myloginapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myloginapplication.model.UserAuthService

class RegistrationViewModelFactory (val userAuthService: UserAuthService): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RegistrationViewModel(userAuthService) as T
    }
}