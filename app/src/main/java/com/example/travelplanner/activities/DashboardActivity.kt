package com.example.travelplanner.activities

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.cardview.widget.CardView
import com.example.travelplanner.R
import com.example.travelplanner.databinding.ActivityDashboardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DashboardActivity : DrawerBaseActivity(){

    lateinit var activityDashboardBinding: ActivityDashboardBinding

    private lateinit var fStore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUserId: String

    private lateinit var tripsCard: CardView
    private lateinit var profileCard: CardView
    private lateinit var logoutCard: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityDashboardBinding = ActivityDashboardBinding.inflate(layoutInflater)
        allocationActivityTitle("Dashboard")
        setContentView(activityDashboardBinding.root)

        auth = Firebase.auth

        tripsCard = findViewById(R.id.dashboard_trips)
        profileCard = findViewById(R.id.dashboard_profile)
        logoutCard = findViewById(R.id.dashboard_logout)

        tripsCard.setOnClickListener {
            val intent = Intent(applicationContext, TripsActivity::class.java)
            startActivity(intent)
        }

        profileCard.setOnClickListener {
            val intent = Intent(applicationContext, ProfileActivity::class.java)
            startActivity(intent)
        }

        logoutCard.setOnClickListener {
            auth.signOut()
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
        }

    }//end onCreate()

}//end class