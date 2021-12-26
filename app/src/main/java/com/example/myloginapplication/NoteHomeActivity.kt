package com.example.myloginapplication

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class NoteHomeActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var toggle: ActionBarDrawerToggle
    lateinit var profileSetting: ImageButton
    val PICTURE_REQUEST = 1
    private lateinit var userEmailId: TextView
    private lateinit var displayUserName: TextView
    private lateinit var userProfilePhoto: ShapeableImageView
    private lateinit var storageRef: StorageReference
    private lateinit var databaseRef: DatabaseReference
    private lateinit var uploadImage: Button
    private lateinit var imageUri: Uri
    private lateinit var progressBar: ProgressBar
    private lateinit var fileReference: StorageReference
    private val noteService = NoteServices()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_home)

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navView)
        toolbar = findViewById(R.id.toolBarView)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser!!
        profileSetting = findViewById(R.id.profile)
        storageRef = FirebaseStorage.getInstance().getReference("ProfileImage/${firebaseUser.uid}/")
        databaseRef =
            FirebaseDatabase.getInstance().getReference("ProfileImage/${firebaseUser.uid}/")

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

    @Suppress("DEPRECATION")
    private fun onProfileButtonClicked() {
        profileSetting.setOnClickListener() {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.profile_view_layout)
            val closeBtn = dialog.findViewById<ImageButton>(R.id.close)
            userEmailId = dialog.findViewById(R.id.emailDisplay)
            displayUserName = dialog.findViewById(R.id.userNameDisplay)
            userProfilePhoto = dialog.findViewById(R.id.profilePicture)
            displayUserName.text = firebaseUser.displayName.toString()
            userEmailId.text = firebaseUser.email.toString()
            uploadImage = dialog.findViewById(R.id.upload)
            uploadImage.isEnabled = false
            progressBar = dialog.findViewById(R.id.bar)
            progressBar.visibility = View.INVISIBLE
            fileReference =
                storageRef.child("image")
            noteService.retrieveImage(this, storageRef, userProfilePhoto)
            userProfilePhoto.setOnClickListener {
                openFileManager()
            }
            uploadImage.setOnClickListener {
                progressBar.visibility = View.VISIBLE
                noteService.deleteFiles(fileReference)
                noteService.uploadFile(this, fileReference, imageUri, databaseRef, progressBar)
            }
            closeBtn.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    @Suppress("DEPRECATION")
    private fun openFileManager() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICTURE_REQUEST)
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICTURE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data!!
            Glide.with(this).load(imageUri).into(userProfilePhoto)
            uploadImage.isEnabled = true
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