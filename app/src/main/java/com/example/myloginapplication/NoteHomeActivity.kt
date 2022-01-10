package com.example.myloginapplication

import android.app.Dialog
import android.content.Context
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
    lateinit var profileSetting: ShapeableImageView
    private val PICTURE_REQUEST = 1
    private lateinit var userEmailId: TextView
    private lateinit var displayUserName: TextView
    private lateinit var userProfilePhoto: ShapeableImageView
    private lateinit var storageRef: StorageReference
    private lateinit var databaseRef: DatabaseReference
    private lateinit var uploadImage: Button
    private lateinit var imageUri: Uri
    private lateinit var progressBar: ProgressBar
    private lateinit var fileReference: StorageReference
    private lateinit var expandableListView: ExpandableListView
    private val noteService = NoteServices()
    private val labelService = LabelServices()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_home)
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navView)
        toolbar = findViewById(R.id.toolBarView)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser!!
        profileSetting = findViewById(R.id.profile)
        expandableListView = findViewById(R.id.expandableListView)
        storageRef = FirebaseStorage.getInstance().getReference("ProfileImage/${firebaseUser.uid}/")
        databaseRef =
            FirebaseDatabase.getInstance().getReference("ProfileImage/${firebaseUser.uid}/")
        fileReference =
            storageRef.child("image")
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Notes"
        noteService.retrieveImage(this, storageRef, profileSetting)
        navigationView.bringToFront()
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState()
        showList(this)
        onClickDrawerMenu(drawerLayout)
        homeScreen()
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
        profileSetting.setOnClickListener {
            val popupMenu = PopupMenu(this, profileSetting)
            popupMenu.menuInflater.inflate(R.menu.profilemenu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.profileId -> {
                        onClickMyProfile()
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
            popupMenu.show()
        }
    }

    private fun showList(context: Context) {
        labelService.retrieveLabelCollection(context, expandableListView)
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

    @Suppress("DEPRECATION")
    private fun onClickMyProfile() {
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

    private fun onClickDrawerMenu(drawerLayout: DrawerLayout) {
        val notes: TextView = findViewById(R.id.viewNote)
        val archive: TextView = findViewById(R.id.archive)
        val share: TextView = findViewById(R.id.share)
        notes.setOnClickListener {
            drawerLayout.closeDrawers()
            homeScreen()
        }
        archive.setOnClickListener() {
            drawerLayout.closeDrawers()
            supportFragmentManager.beginTransaction()
                .replace(R.id.homeFragment, ArchivePage())
                .commit()
        }
        share.setOnClickListener {
            drawerLayout.closeDrawers()
            Toast.makeText(this, "yet to be implement", Toast.LENGTH_SHORT).show()
        }
    }

    private fun homeScreen() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.homeFragment, NotesView())
            .commit()
    }
}