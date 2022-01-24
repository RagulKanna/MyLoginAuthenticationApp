package com.example.myloginapplication.api

interface LoginListener {
    fun onLogin(response: LoginResponse?, status: Boolean, message: String)
}
