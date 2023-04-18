package com.example.travelplanner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.travelplanner.databinding.ActivityDashboardBinding

class DashboardActivity : DrawerBaseActivity(){

    lateinit var activityDashboardBinding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityDashboardBinding = ActivityDashboardBinding.inflate(layoutInflater)
        allocationActivityTitle("Dashboard")
        setContentView(activityDashboardBinding.root)
    }
}