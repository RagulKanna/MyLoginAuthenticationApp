package com.example.myloginapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myloginapplication.listener.AuthListener
import com.example.myloginapplication.model.User
import com.example.myloginapplication.model.UserAuthService

class RegistrationViewModel(val userAuthService: UserAuthService) : ViewModel() {
    private val _registrationStatus = MutableLiveData<AuthListener>()
    val registrationStatus = _registrationStatus as LiveData<AuthListener>

    fun registrationUser(user: User) {
        userAuthService.registerUser(user) {
            _registrationStatus.value = it
        }
    }

}