package com.example.travelplanner

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.travelplanner.databinding.ActivityDashboardBinding
import com.google.android.libraries.places.api.model.OpeningHours
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

    private lateinit var openingHours: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityDashboardBinding = ActivityDashboardBinding.inflate(layoutInflater)
        allocationActivityTitle("Dashboard")
        setContentView(activityDashboardBinding.root)

        val monday = hashMapOf(
            "close" to "1700",
            "open" to "0900"
        )

        val tuesday = hashMapOf(
            "close" to "1700",
            "open" to "0900"
        )

        val wednesday = hashMapOf(
            "close" to "1700",
            "open" to "0900"
        )

        val thursday = hashMapOf(
            "close" to "1700",
            "open" to "0900"
        )

        val friday = hashMapOf(
            "close" to "1700",
            "open" to "0900"
        )


        val openingHoursList = ArrayList<Any>()
        openingHoursList.add(monday)
        openingHoursList.add(tuesday)
        openingHoursList.add(wednesday)
        openingHoursList.add(thursday)
        openingHoursList.add(friday)

        Log.d(ContentValues.TAG, "openingHoursList: ${openingHoursList}");



        auth = Firebase.auth
        fStore = Firebase.firestore
        currentUserId = auth.currentUser?.uid.toString()

        val placeReference = fStore.collection("users")
            .document(currentUserId)
            .collection("trips")
            .document("1KxIk7fe84RYghxKjVGE")
            .collection("places")
            .document("5seXPNOCIVKQkGqtFo9F")
            .get().addOnSuccessListener {result ->
                Log.d(ContentValues.TAG, "id: ${result.id}");
                Log.d(ContentValues.TAG, "data: ${result.data}");
                val openingHoursArray:ArrayList<*> = result.data?.get("openingHours") as ArrayList<*>
                Log.d(ContentValues.TAG, "opening hours array: ${openingHoursArray}");
                for(day in openingHoursArray){
                    val openingHoursMap = day as Map<*, *>
                    Log.d(ContentValues.TAG, "close: ${openingHoursMap.get("close")}");
                    Log.d(ContentValues.TAG, "open: ${openingHoursMap.get("open")}");
                }
            }



    }
}