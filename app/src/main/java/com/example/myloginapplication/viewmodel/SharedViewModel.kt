package com.example.myloginapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myloginapplication.model.UserAuthService

class SharedViewModel(val userAuthService: UserAuthService) : ViewModel() {
    private val _gotoLoginPageStatus = MutableLiveData<Boolean>()
    val gotoLoginPageStatus = _gotoLoginPageStatus as LiveData<Boolean>

    private val _gotoRegistrationPageStatus = MutableLiveData<Boolean>()
    val gotoRegistrationPageStatus = _gotoRegistrationPageStatus as LiveData<Boolean>

    private val _gotoHomePageStatus = MutableLiveData<Boolean>()
    val gotoHomePageStatus = _gotoHomePageStatus as LiveData<Boolean>

    fun gotoLoginPage(status: Boolean) {
        _gotoLoginPageStatus.value = status
    }

    fun gotoRegistrationPage(status: Boolean) {
        _gotoRegistrationPageStatus.value = status
    }

    fun gotoHomePage(status: Boolean) {
        _gotoHomePageStatus.value = status
    }
}