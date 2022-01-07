package com.example.myloginapplication


import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.myloginapplication.model.UserAuthService
import com.example.myloginapplication.viewmodel.SharedViewModel
import com.example.myloginapplication.viewmodel.SharedViewModelFactory

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var sharedViewModel: SharedViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_main)
        sharedViewModel = ViewModelProvider(
            this,
            SharedViewModelFactory(UserAuthService())
        )[SharedViewModel::class.java]
        sharedViewModel.gotoLoginPage(true)
        //sharedViewModel.gotoHomePage(true)
        observeAppNav()
        //loadLoginPage()
    }

    private fun observeAppNav() {
        sharedViewModel.gotoLoginPageStatus.observe(this, Observer {
            if (it == true) {
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.main_fragment_container, LoginPage())
                        .commit()
                }
            }
        })

        sharedViewModel.gotoRegistrationPageStatus.observe(this, Observer {
            if (it == true) {
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.login_fragment, RegistrationPage())
                }
            }
        })

        sharedViewModel.gotoHomePageStatus.observe(this, Observer {
            if (it == true) {
                startActivity(Intent(this, NoteHomeActivity::class.java))
            }
        })
    }

    private fun loadLoginPage() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.main_fragment_container, LoginPage())
        }
    }

    override fun onBackPressed() {
        val a = Intent(Intent.ACTION_MAIN)
        a.addCategory(Intent.CATEGORY_HOME)
        a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(a)
    }
}