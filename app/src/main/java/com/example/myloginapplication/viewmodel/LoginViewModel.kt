package com.example.myloginapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myloginapplication.listener.AuthListener
import com.example.myloginapplication.model.UserAuthService

class LoginViewModel(val userAuthService: UserAuthService) : ViewModel() {

    private val _loginStatus = MutableLiveData<AuthListener>()
    val loginStatus = _loginStatus as LiveData<AuthListener>

    fun loginToFundoo(emailId: String, password: String) {
        userAuthService.loginUser(emailId, password) {
            _loginStatus.value = it
        }
    }

    fun loginWithApi(emailId: String, password: String){
        userAuthService.loginWithRestApi(emailId, password){
            _loginStatus.value = it
        }
    }
}
