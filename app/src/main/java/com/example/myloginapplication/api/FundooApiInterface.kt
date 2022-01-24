package com.example.myloginapplication.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface FundooApiInterface {
    @POST("./accounts:signInWithPassword?key=AIzaSyBUIyeuBY-XP-8p6lf_vkEDcWuMbSfjfq4")
    fun loginFundooUser(@Body request: LoginRequest): Call<LoginResponse>
}