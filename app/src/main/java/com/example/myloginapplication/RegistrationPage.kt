package com.example.myloginapplication


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth

class RegistrationPage : Fragment() {

    private lateinit var haveAnAccountButton: TextView
    private lateinit var emailText: EditText
    private lateinit var passwordText: EditText
    private lateinit var confirmPasswordText: EditText
    private lateinit var registerButton: Button
    private lateinit var emailCheck: Regex
    private lateinit var mAuth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_registration, container, false)

        haveAnAccountButton = view.findViewById(R.id.moveToLoginPage)
        emailText = view.findViewById(R.id.EmailAddressInput)
        passwordText = view.findViewById(R.id.PasswordInput)
        confirmPasswordText = view.findViewById(R.id.ConfirmPasswordInput)
        registerButton = view.findViewById(R.id.Register_Button)

        emailCheck =
            Regex("^[a-z0-9]{1,}+([_+-.][a-z0-9]{3,}+)*@[a-z0-9]+.[a-z]{2,3}+(.[a-z]{2,3}){0,1}$")
        mAuth = FirebaseAuth.getInstance()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        performAuthentication()
        moveToLogin()
    }

    private fun moveToLogin() {
        haveAnAccountButton.setOnClickListener {
            childFragmentManager.beginTransaction()
                .replace(R.id.registration_Layout, LoginPage()).commit()
        }
    }

    private fun sendUserToHomePage() {
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
    }

    private fun performAuthentication() {
        registerButton.setOnClickListener {
            val email = emailText.text.toString()
            val password = passwordText.text.toString()
            val confirmPassword = confirmPasswordText.text.toString()

            if (!email.matches(emailCheck)) {
                emailText.error = "Enter correct Email"
            } else if (password == "" || password.length < 6) {
                passwordText.error = "Enter correct password"
            } else if (password != confirmPassword) {
                passwordText.error = "Password does not match"
                confirmPasswordText.error = "Password does not match"
            } else {
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                    OnCompleteListener {
                        if (it.isSuccessful) {
                            sendUserToHomePage()
                            Toast.makeText(
                                activity,
                                "Registration Successful",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        } else {
                            Toast.makeText(activity, "" + it.exception, Toast.LENGTH_SHORT)
                                .show()
                        }
                    })
            }
        }
    }


}


