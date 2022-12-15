package com.example.travelplanner

import android.content.ContentValues.TAG
import android.content.Intent
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Document

class TripsActivity : AppCompatActivity() {

    private lateinit var fStore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trips)

        auth = Firebase.auth
        fStore = Firebase.firestore

        val currentUserId = auth.currentUser?.uid

        displayTrips(currentUserId)
    }

    private fun displayTrips(currentUserId: String?) {
        if(currentUserId != null){
            val userReference: DocumentReference = fStore.collection("users").document(currentUserId.toString())
            userReference.collection("trips").get()
                .addOnSuccessListener { result ->
                    listView = findViewById(R.id.trips_list)
                    val tripsList: ArrayList<String> = ArrayList()
                    val arrayAdapter: ArrayAdapter<*>
                    for(document in result){
                        Log.d(TAG, "${document.id} => ${document.data.get("trip_name")}")
                        val tripName = document.data.get("trip_name").toString()
                        val tripCoordinates: GeoPoint = document.data.get("coordinates") as GeoPoint
                        tripsList.add(tripName)
                    }
                    arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, tripsList)
                    listView.adapter = arrayAdapter
                    listView.setOnItemClickListener{parent, view, position, id ->
                        val intent = Intent(this, PlaceActivity::class.java)
                        intent.putExtra("trips", tripsList)
                        intent.putExtra("position", position)
                        startActivity(intent)
                    }
                }
                .addOnFailureListener{
                    Log.w(TAG, "Error getting documents.")
                    Toast.makeText(applicationContext, "Failure", Toast.LENGTH_LONG).show()
                }
        }
    }

}