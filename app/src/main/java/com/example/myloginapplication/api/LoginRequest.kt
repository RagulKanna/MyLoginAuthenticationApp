package com.example.myloginapplication.api

data class LoginRequest(var email: String = "", var password: String = "", var returnSecureToken: Boolean)
