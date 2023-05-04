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
    var clickedNavItem: Int = 0

    override fun setContentView(view: View?) {
        drawerLayout = layoutInflater.inflate(R.layout.activity_drawer_base, null) as DrawerLayout
        val container:FrameLayout = drawerLayout.findViewById(R.id.activityContainer)
        container.addView(view)
        super.setContentView(drawerLayout)

        val toolBar: Toolbar = drawerLayout.findViewById(R.id.toolBar)
        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.menu_Open, R.string.close_menu)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        val navigationView:NavigationView = drawerLayout.findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { item ->

            when(item.itemId){
                R.id.nav_home -> {
                    Log.d("MENU_DRAWER_TAG", "Home Item Selected");
                    clickedNavItem = R.id.nav_home
                }
                R.id.nav_trips -> {
                    Log.d("MENU_DRAWER_TAG", "Trips Item Selected");
                    clickedNavItem = R.id.nav_trips
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
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }//end NavigationItemSelectedListener()



        //adding a drawerListener helps prevent lag when switching activities in Nav Drawer
        drawerLayout.addDrawerListener(object: DrawerLayout.DrawerListener{
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

            override fun onDrawerOpened(drawerView: View) {}

            override fun onDrawerStateChanged(newState: Int) {}


            override fun onDrawerClosed(drawerView: View) {
                when(clickedNavItem){
                    R.id.nav_home -> {
                        val intent = Intent(applicationContext, DashboardActivity::class.java)
                        startActivity(intent)
                    }
                    R.id.nav_trips -> {
                        val intent = Intent(applicationContext, TripsActivity::class.java)
                        startActivity(intent)
                    }
                }
            }


        })//end DrawerListener()


    }//end setContentView()



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            true
        }
        return super.onOptionsItemSelected(item)
    }//end onOptionsItemSelected()

    //each activity sets the action bar title
    fun allocationActivityTitle(titleString:String){
        if(supportActionBar != null) {
            supportActionBar!!.title = titleString
        }
    }//end allocationActivityTitle()



}//end class