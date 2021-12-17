package com.example.myloginapplication

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.util.*


class NoteHomeActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var toggle: ActionBarDrawerToggle
    lateinit var mAuth: FirebaseAuth
    lateinit var profileSetting: ImageButton
    //  private lateinit var userEmailId: TextView
    //   private lateinit var displayUserName: TextView
    // private lateinit var userProfilePhoto: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_home)

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navView)
        toolbar = findViewById(R.id.toolBarView)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser!!
        profileSetting = findViewById(R.id.profile)

        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        var googleSignInClient = GoogleSignIn.getClient(this, gso)
        val account = GoogleSignIn.getLastSignedInAccount(this)

//        userProfilePhoto = findViewById(R.id.profileImage)
        //  displayUserName = findViewById(R.id.userName)
        //  userEmailId = findViewById(R.id.userEmail)
        //  displayUserName.text = account.displayName
        // userEmailId.text = account.email
        // Glide.with(this).load(account.photoUrl).into(userProfilePhoto)

        val user = FirebaseAuth.getInstance()
        //userEmailId.text = user.currentUser?.email
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Notes"

        navigationView.bringToFront()
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState()

        homeScreen()
        onNavigationItemSelected(navigationView, drawerLayout)
        onProfileButtonClicked()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        toggle.onOptionsItemSelected(item)
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
        super.onBackPressed()
    }

private fun onProfileButtonClicked() {
    profileSetting.setOnClickListener() {
        val popupMenu = PopupMenu(this, profileSetting)
        popupMenu.menuInflater.inflate(R.menu.toolbar_profile_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
            when (it.itemId) {
                R.id.changeProfilePicture -> Toast.makeText(
                    this,
                    "profileImage",
                    Toast.LENGTH_SHORT
                ).show()
                R.id.myProfile -> Toast.makeText(this, "My profile", Toast.LENGTH_SHORT).show()
            }
            true
        })
        popupMenu.show()
    }
}

private fun onNavigationItemSelected(
    navigationView: NavigationView,
    drawerLayout: DrawerLayout
) {
    navigationView.setNavigationItemSelectedListener {
        when (it.itemId) {
            R.id.notes -> {
                drawerLayout.closeDrawers()
                homeScreen()

            }
            R.id.archived_Notes -> {
                drawerLayout.closeDrawers()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.homeFragment, ArchivePage())
                    .commit()

            }
            R.id.logout -> {
                firebaseAuth.signOut()
                finish()
                startActivity(Intent(applicationContext, MainActivity::class.java))
                Toast.makeText(applicationContext, "Signed Out", Toast.LENGTH_SHORT).show()
            }
        }
        true
    }
}

private fun homeScreen() {
    supportFragmentManager.beginTransaction()
        .replace(R.id.homeFragment, NotesView())
        .commit()
}

}