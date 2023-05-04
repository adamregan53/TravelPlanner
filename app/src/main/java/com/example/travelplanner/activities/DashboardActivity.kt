package com.example.travelplanner.activities

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityDashboardBinding = ActivityDashboardBinding.inflate(layoutInflater)
        allocationActivityTitle("Dashboard")
        setContentView(activityDashboardBinding.root)



    }//end onCreate()

}//end class