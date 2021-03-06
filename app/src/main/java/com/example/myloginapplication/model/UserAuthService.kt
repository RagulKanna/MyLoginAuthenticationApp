package com.example.myloginapplication.model

import android.util.Log
import com.example.myloginapplication.api.Constant
import com.example.myloginapplication.api.LoginListener
import com.example.myloginapplication.api.LoginLoader
import com.example.myloginapplication.api.LoginResponse
import com.example.myloginapplication.listener.AuthListener
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class UserAuthService {

    var mAuth: FirebaseAuth

    init {
        mAuth = FirebaseAuth.getInstance()
    }

    fun loginUser(emailId: String = "", password: String = "", listener: (AuthListener) -> Unit) {

        mAuth.signInWithEmailAndPassword(emailId, password).addOnCompleteListener(
            OnCompleteListener {
                if (it.isSuccessful) {
                    listener(AuthListener(it.isSuccessful, "Login Successful"))
                }
            }
        )
    }

    fun registerUser(user: User, listener: (AuthListener) -> Unit) {
        mAuth.createUserWithEmailAndPassword(user.emailId, user.password).addOnCompleteListener(
            OnCompleteListener {
                if (it.isSuccessful) {
                    val profileUpdates: UserProfileChangeRequest =
                        UserProfileChangeRequest.Builder().setDisplayName(user.name).build()
                    mAuth.currentUser?.updateProfile(profileUpdates)
                    listener(AuthListener(it.isSuccessful, "Registration Successful"))
                }
            })
    }

    fun loginWithRestApi(emailId: String, password: String, listener: (AuthListener) -> Unit) {
        val loginLoader = LoginLoader()
        loginLoader.getLoginDone(object : LoginListener {
            override fun onLogin(response: LoginResponse?, status: Boolean, message: String) {
                if (status) {
                    if (response != null) {
                        Constant.getInstance().setUserId(response.localId)
                        listener(AuthListener(status = status, message = message))
                    }
                }
            }
        }, emailId, password)
    }
}
