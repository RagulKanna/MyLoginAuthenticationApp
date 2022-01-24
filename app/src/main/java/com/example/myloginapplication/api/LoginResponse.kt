package com.example.myloginapplication.api

data class LoginResponse(var idToken: String, var email: String, var refreshToken: String, var expiresIn: String, var localId: String, var registered: Boolean)
