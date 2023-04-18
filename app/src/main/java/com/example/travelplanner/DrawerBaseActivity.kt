package com.example.travelplanner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text

open class DrawerBaseActivity : AppCompatActivity() {
    lateinit var drawerLayout: DrawerLayout
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    override fun setContentView(view: View?) {
        drawerLayout = layoutInflater.inflate(R.layout.activity_drawer_base, null) as DrawerLayout
        val container:FrameLayout = drawerLayout.findViewById(R.id.activityContainer)
        container.addView(view)
        super.setContentView(drawerLayout)

        val toolBar: Toolbar = drawerLayout.findViewById(R.id.toolBar)
        setSupportActionBar(toolBar)

        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.menu_Open, R.string.close_menu)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        val navigationView:NavigationView = drawerLayout.findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { item ->
            drawerLayout.closeDrawer(GravityCompat.START)
            when(item.itemId){
                R.id.nav_home -> {
                    Log.d("MENU_DRAWER_TAG", "Home Item Selected");
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_trips -> {
                    Log.d("MENU_DRAWER_TAG", "Trips Item Selected");
                    val intent = Intent(this, TripsActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_settings -> {
                    Log.d("MENU_DRAWER_TAG", "Settings Item Selected");

                }
                R.id.nav_profile -> {
                    Log.d("MENU_DRAWER_TAG", "Profile Item Selected");

                }
                R.id.nav_logout -> {
                    Log.d("MENU_DRAWER_TAG", "Logout Item Selected");

                }
            }
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            true
        }
        return super.onOptionsItemSelected(item)
    }

    fun allocationActivityTitle(titleString:String){
        if(supportActionBar != null) {
            supportActionBar!!.title = titleString
        }
    }
}