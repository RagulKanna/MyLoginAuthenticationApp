package com.example.myloginapplication


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.myloginapplication.model.User
import com.example.myloginapplication.model.UserAuthService
import com.example.myloginapplication.viewmodel.RegistrationViewModel
import com.example.myloginapplication.viewmodel.RegistrationViewModelFactory
import com.example.myloginapplication.viewmodel.SharedViewModel
import com.example.myloginapplication.viewmodel.SharedViewModelFactory

class RegistrationPage : Fragment() {

    private lateinit var haveAnAccountButton: TextView
    private lateinit var emailText: EditText
    private lateinit var passwordText: EditText
    private lateinit var confirmPasswordText: EditText
    private lateinit var userNameText: EditText
    private lateinit var registerButton: Button
    private lateinit var emailCheck: Regex
    private lateinit var registrationViewModel: RegistrationViewModel
    private lateinit var sharedViewModel: SharedViewModel

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
        userNameText = view.findViewById(R.id.userName)
        emailCheck =
            Regex("^[a-z0-9]{1,}+([_+-.][a-z0-9]{3,}+)*@[a-z0-9]+.[a-z]{2,3}+(.[a-z]{2,3}){0,1}$")
        registrationViewModel = ViewModelProvider(
            this,
            RegistrationViewModelFactory(UserAuthService())
        ).get(RegistrationViewModel::class.java)
        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory(UserAuthService())
        )[SharedViewModel::class.java]
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

    private fun performAuthentication() {
        registerButton.setOnClickListener {
            val emailId = emailText.text.toString()
            val password = passwordText.text.toString()
            val confirmPassword = confirmPasswordText.text.toString()
            val userName = userNameText.text.toString()
            val user = User(emailId = emailId, password = password, name = userName)
            if (!emailId.matches(emailCheck)) {
                emailText.error = "Enter correct Email"
            } else if (password == "" || password.length < 6) {
                passwordText.error = "Enter correct password"
            } else if (password != confirmPassword) {
                passwordText.error = "Password does not match"
                confirmPasswordText.error = "Password does not match"
            } else {
                registrationViewModel.registrationUser(user)
                registrationViewModel.registrationStatus.observe(viewLifecycleOwner, Observer {
                    if (it.status) {
                        sharedViewModel.gotoHomePage(true)
                    } else {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }
}


