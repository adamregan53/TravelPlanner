package com.example.travelplanner

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class TripsActivity : AppCompatActivity() {

    private lateinit var fStore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var newRecyclerView: RecyclerView
    private lateinit var tripsList: ArrayList<TripDetails>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trips)

        auth = Firebase.auth
        fStore = Firebase.firestore

        tripsList = arrayListOf<TripDetails>()

        val currentUserId = auth.currentUser?.uid

        displayTrips(currentUserId)
    }

    private fun displayTrips(currentUserId: String?) {
        if(currentUserId != null){
            val userReference: DocumentReference = fStore.collection("users").document(currentUserId.toString())//get user document reference from Firebase
            userReference.collection("trips").get()
                .addOnSuccessListener { result ->

                    newRecyclerView = findViewById(R.id.recyclerView)
                    newRecyclerView.layoutManager = LinearLayoutManager(this)
                    newRecyclerView.setHasFixedSize(true)

                    for(document in result){
                        Log.d(TAG, "${document.id} => ${document.data["trip_name"]}")
                        val tripId = document.id
                        val tripName = document.data["trip_name"].toString()
                        val tripCoordinates: GeoPoint = document.data.get("coordinates") as GeoPoint
                        val tripDetail = TripDetails(tripId, tripName, tripCoordinates)
                        tripsList.add(tripDetail)
                    }

                    var adapter = TripAdapter(tripsList)
                    newRecyclerView.adapter = adapter
                    adapter.setOnItemClickListener(object: TripAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {
                            //Toast.makeText(this@TripsActivity, "You clicked on ${tripsList[position].trip_name}", Toast.LENGTH_SHORT).show()
                            val tripId = tripsList[position].id
                            val tripName = tripsList[position].name
                            val coordinates = tripsList[position].coordinates
                            Log.w(TAG, "tripName: $tripName, coordinates: $coordinates")

                            val intent =Intent(this@TripsActivity, MapsActivity::class.java)
                            intent.putExtra("tripId", tripId)
                            intent.putExtra("tripName", tripName)
                            intent.putExtra("latitude", coordinates.latitude)
                            intent.putExtra("longitude", coordinates.longitude)

                            startActivity(intent)
                        }

                    })

                }
                .addOnFailureListener{
                    Log.w(TAG, "Error getting documents.")
                    Toast.makeText(applicationContext, "Failure", Toast.LENGTH_LONG).show()
                }
        }
    }

}