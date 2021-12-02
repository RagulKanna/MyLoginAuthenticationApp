package com.example.myloginapplication

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class LoginPage : Fragment() {

    private lateinit var createAccount: TextView
    private lateinit var emailText: EditText
    private lateinit var passwordText: EditText
    private lateinit var forgotPassword: TextView
    private lateinit var loginButton: TextView
    private lateinit var googleButton: TextView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mUser: FirebaseUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        createAccount = view.findViewById(R.id.createNew)
        emailText = view.findViewById(R.id.EmailAddressInput)
        passwordText = view.findViewById(R.id.PasswordInput)
        forgotPassword = view.findViewById(R.id.forgotPassword)
        loginButton = view.findViewById(R.id.loginButton)
        googleButton = view.findViewById(R.id.google_icon)
        mAuth = FirebaseAuth.getInstance()

        createAccount.setOnClickListener() {
            childFragmentManager.beginTransaction()
                .replace(R.id.login_fragment, RegistrationPage()).commit()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        performLogin()
        googleLogin()
        recoverForgotPassword()
    }

    private fun recoverForgotPassword() {
        forgotPassword.setOnClickListener(View.OnClickListener {
            val resetMail = EditText(it.context)
            val passwordResetDialog = AlertDialog.Builder(context)
            passwordResetDialog.setTitle("Reset Password?")
            passwordResetDialog.setMessage("Enter Email to send the Reset Link:")
            passwordResetDialog.setView(resetMail)

            passwordResetDialog.setPositiveButton(
                "Yes",
                DialogInterface.OnClickListener { dialog, which ->
                    val mail = resetMail.text.toString()
                    mAuth.sendPasswordResetEmail(mail).addOnSuccessListener {
                        Toast.makeText(
                            context,
                            "Reset Link is Sent Successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }.addOnFailureListener(OnFailureListener {
                        Toast.makeText(context, "Reset Link is not Sent!! $it", Toast.LENGTH_SHORT)
                            .show()
                    })
                })
            passwordResetDialog.create().show()
        })
    }


    private fun performLogin() {
        loginButton.setOnClickListener {
            val email = emailText.text.toString()
            val password = passwordText.text.toString()
            val emailCheck =
                Regex("^[a-z0-9]{1,}+([_+-.][a-z0-9]{3,}+)*@[a-z0-9]+.[a-z]{2,3}+(.[a-z]{2,3}){0,1}$")

            if (!email.matches(emailCheck)) {
                emailText.error = "Enter correct Email"
            } else if (password == "" || password.length < 6) {
                passwordText.error = "Enter correct password"
            } else {
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                    OnCompleteListener {
                        if (it.isSuccessful) {
                            sendUserToSuccessfulPage()
                            Toast.makeText(
                                activity,
                                "Login Successful",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        } else {
                            Toast.makeText(activity, "" + it.exception, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                )
            }
        }
    }

    private fun sendUserToSuccessfulPage() {
       val intent = Intent(context, HomeActivity::class.java)
        startActivity(intent)
    }

    private fun googleLogin() {
        googleButton.setOnClickListener() {
            childFragmentManager.beginTransaction()
                .replace(R.id.login_fragment, GoogleSignInFragment()).commit()
        }
    }
}



