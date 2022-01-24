package com.example.myloginapplication.api

object Constant {
    private lateinit var userId: String
    private var constant: Constant? = null

    fun getInstance(): Constant {
        if (constant == null) {
            constant = Constant
        }
        return constant!!
    }
    fun setUserId(userId: String){
        this.userId =userId
    }
}