package com.example.travelplanner

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
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
    private lateinit var currentUserId: String

    private lateinit var newRecyclerView: RecyclerView

    private lateinit var tripId: String
    private lateinit var tripName: String
    private lateinit var tripCoordinates: GeoPoint
    private lateinit var tripsList: ArrayList<TripDetails>

    private lateinit var newTripBtn: Button
    private lateinit var viewTripBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trips)

        auth = Firebase.auth
        fStore = Firebase.firestore
        currentUserId = auth.currentUser?.uid.toString()

        newRecyclerView = findViewById(R.id.recyclerView)

        tripsList = arrayListOf<TripDetails>()
        newTripBtn = this.findViewById(R.id.new_trip_btn)

        retrieveTrips()

    }//end onCreate()


    private fun retrieveTrips() {
        if(tripsList.isEmpty()){
            //reference to user document in database
            val userReference: DocumentReference = fStore.collection("users").document(currentUserId.toString())
            userReference.collection("trips").get()
                .addOnSuccessListener { result ->
                    //get data from documents in trips collection
                    for(document in result){
                        Log.d(TAG, "${document.id} => ${document.data["trip_name"]}")
                        tripId = document.id
                        tripName = document.data["trip_name"].toString()
                        tripCoordinates = document.data.get("coordinates") as GeoPoint
                        val tripDetail = TripDetails(tripId, tripName, tripCoordinates)
                        tripsList.add(tripDetail)
                        Log.w(TAG, "trips activity list: ${Singleton.myString}")
                    }

                    displayTrips()

                }
                .addOnFailureListener{
                    Log.w(TAG, "Error getting documents.")
                    Toast.makeText(applicationContext, "Failure", Toast.LENGTH_LONG).show()
                }
        }else{
            Toast.makeText(this, "No trips available", Toast.LENGTH_SHORT).show()
        }
    }//end retrieveTrips()



    private fun displayTrips() {

        newRecyclerView.layoutManager = LinearLayoutManager(this)

        //custom adapter for TripDetails data class
        var adapter = TripAdapter(tripsList)
        newRecyclerView.adapter = adapter
        adapter.setOnItemClickListener(object: TripAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                //Toast.makeText(this@TripsActivity, "You clicked on ${tripsList[position].trip_name}", Toast.LENGTH_SHORT).show()
                val tripId = tripsList[position].id
                val tripName = tripsList[position].name
                val coordinates = tripsList[position].coordinates
                Log.w(TAG, "tripId: $tripId, tripName: $tripName, coordinates: $coordinates")


                val intent =Intent(this@TripsActivity, PlaceActivity::class.java)
                intent.putExtra("tripId", tripId)
                /*
                intent.putExtra("tripName", tripName)
                intent.putExtra("latitude", coordinates.latitude)
                intent.putExtra("longitude", coordinates.longitude)
                */
                startActivity(intent)
            }

        })
    }//end displayTrips()

}//end class