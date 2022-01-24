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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.myloginapplication.model.UserAuthService
import com.example.myloginapplication.viewmodel.LoginViewModel
import com.example.myloginapplication.viewmodel.LoginViewModelFactory
import com.example.myloginapplication.viewmodel.SharedViewModel
import com.example.myloginapplication.viewmodel.SharedViewModelFactory
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
    private lateinit var loginViewModel: LoginViewModel
    lateinit var sharedViewModel: SharedViewModel

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
        loginViewModel = ViewModelProvider(
            this,
            LoginViewModelFactory(UserAuthService())
        ).get(LoginViewModel::class.java)
        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory(UserAuthService())
        )[SharedViewModel::class.java]
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        performLogin()
        googleLogin()
        recoverForgotPassword()
        goToRegisterPage()
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

    private fun goToRegisterPage() {
        createAccount.setOnClickListener {
            childFragmentManager.beginTransaction().replace(R.id.login_fragment, RegistrationPage())
                .commit()
        }
    }

    private fun performLogin() {
        loginButton.setOnClickListener {
            val emailId = emailText.text.toString()
            val password = passwordText.text.toString()
            val emailCheck =
                Regex("^[a-z0-9]{1,}+([_+-.][a-z0-9]{3,}+)*@[a-z0-9]+.[a-z]{2,3}+(.[a-z]{2,3}){0,1}$")

            if (!emailId.matches(emailCheck)) {
                emailText.error = "Enter correct Email"
            } else if (password == "" || password.length < 6) {
                passwordText.error = "Enter correct password"
            } else {
                loginViewModel.loginWithApi(emailId, password)
                loginViewModel.loginStatus.observe(viewLifecycleOwner, Observer {
                    if (it.status) {
                        sharedViewModel.gotoHomePage(true)
                    }
                })
            }
        }
    }

    private fun sendUserToSuccessfulPage() {
        startActivity(Intent(context, NoteHomeActivity::class.java))
    }

    private fun googleLogin() {
        googleButton.setOnClickListener() {
            childFragmentManager.beginTransaction()
                .replace(R.id.login_fragment, GoogleSignInFragment()).commit()
        }
    }

    private fun firebaseLogin(emailId: String, password: String) {
        mAuth.signInWithEmailAndPassword(emailId, password).addOnCompleteListener(
            OnCompleteListener {
                if (it.isSuccessful) {
                    sendUserToSuccessfulPage()
                } else {
                    Toast.makeText(requireContext(), "${it.exception}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        )
    }
}



