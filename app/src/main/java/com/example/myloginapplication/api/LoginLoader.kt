package com.example.myloginapplication.api

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginLoader {
    fun getLoginDone(authListener: LoginListener, email: String, password: String) {
        FundooClient.getInstance()
        FundooClient.getMyApi().loginFundooUser(LoginRequest(email, password, true))
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if(response.isSuccessful){
                        authListener.onLogin(response.body()!!,true, "")
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    authListener.onLogin(null, false, t.message.toString())
                }

            })
    }
}