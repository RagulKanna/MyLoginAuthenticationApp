package com.example.myloginapplication.api

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object FundooClient {
    private lateinit var myApi: FundooApiInterface
    private lateinit var client: OkHttpClient
    private var instance: FundooClient? = null

    init {
        val httpClient = OkHttpClient.Builder()
        client = httpClient.connectTimeout(40, TimeUnit.SECONDS).readTimeout(40, TimeUnit.SECONDS)
            .writeTimeout(40, TimeUnit.SECONDS).build()
    }

    fun getInstance(): FundooClient? {
        if (instance == null) {
            instance = FundooClient
        }
        return instance
    }

    fun getMyApi(): FundooApiInterface {
        val retrofit = Retrofit.Builder().baseUrl("https://identitytoolkit.googleapis.com/v1/")
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .client(client).build()
        myApi = retrofit.create(FundooApiInterface::class.java)
        return myApi
    }
}