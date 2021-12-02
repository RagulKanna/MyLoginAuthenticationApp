package com.example.myloginapplication

import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.ListView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

lateinit var toggle: ActionBarDrawerToggle

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var frameLayout = findViewById<FrameLayout>(R.id.frame)
        var navigationView = findViewById<NavigationView>(R.id.navigationView)
        var drawerLayout = findViewById<DrawerLayout>(R.id.drawer)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        toggle.syncState()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }
}