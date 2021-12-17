package com.example.myloginapplication.model

import com.example.myloginapplication.listener.AuthListener
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth

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
                    listener(AuthListener(it.isSuccessful, "Registration Sucessful"))
                }
            })
    }
}
