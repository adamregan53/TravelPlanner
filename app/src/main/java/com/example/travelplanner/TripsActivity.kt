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
    //private lateinit var listView: ListView

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
                        Log.d(TAG, "${document.id} => ${document.data.get("trip_name")}")
                        val tripName = document.data.get("trip_name").toString()
                        val tripCoordinates: GeoPoint = document.data.get("coordinates") as GeoPoint
                        val tripDetail: TripDetails = TripDetails(tripName, tripCoordinates)
                        tripsList.add(tripDetail)
                    }

                    newRecyclerView.adapter = TripAdapter(tripsList)


                    /*
                    listView.setOnItemClickListener{ parent: AdapterView<*>, view: View, position: Int, id: Long ->
                        val tripName = tripsList[position].trip_name
                        val coordinates = tripsList[position].coordinates
                        Log.w(TAG, "tripName: $tripName, coordinates: $coordinates")

                        val intent = Intent(this, PlaceActivity::class.java)
                        intent.putExtra("tripName", tripName)
                        intent.putExtra("latitude", coordinates.latitude)
                        intent.putExtra("longitude", coordinates.longitude)

                        startActivity(intent)

                    }*/
                }
                .addOnFailureListener{
                    Log.w(TAG, "Error getting documents.")
                    Toast.makeText(applicationContext, "Failure", Toast.LENGTH_LONG).show()
                }
        }
    }

}