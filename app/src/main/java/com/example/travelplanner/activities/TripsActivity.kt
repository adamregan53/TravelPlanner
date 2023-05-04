package com.example.travelplanner.activities

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travelplanner.R
import com.example.travelplanner.data.Singleton
import com.example.travelplanner.adapters.TripAdapter
import com.example.travelplanner.data.TripDetails
import com.example.travelplanner.databinding.ActivityTripsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class TripsActivity : DrawerBaseActivity(){//end class

    //firebase
    private lateinit var fStore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUserId: String

    //recycler view
    private lateinit var tripsRecyclerView: RecyclerView

    //trip data
    private lateinit var tripId: String
    private lateinit var tripName: String
    private lateinit var tripCoordinates: GeoPoint
    private lateinit var tripsList: ArrayList<TripDetails>

    //layout
    private lateinit var newTripBtn: Button
    private lateinit var activityTripsBinding: ActivityTripsBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityTripsBinding = ActivityTripsBinding.inflate(layoutInflater)
        allocationActivityTitle("Trips")
        setContentView(activityTripsBinding.root)

        auth = Firebase.auth
        fStore = Firebase.firestore
        currentUserId = auth.currentUser?.uid.toString()

        tripsRecyclerView = findViewById(R.id.tripsRecyclerView)

        //tripsList = arrayListOf<TripDetails>()
        tripsList = Singleton.tripsList
        newTripBtn = this.findViewById(R.id.new_trip_btn)

        retrieveTrips()

    }//end onCreate()



    private fun retrieveTrips() {
        if(Singleton.tripsList.isEmpty()){
            //reference to user document in database
            Log.d(TAG, "tripsList isEmpty: ${Singleton.tripsList.isEmpty()}");

            val userReference: DocumentReference = fStore.collection("users").document(currentUserId)
            userReference.collection("trips").get()
                .addOnSuccessListener { result ->
                    //get data from documents in trips collection
                    for(document in result){
                        Log.d(TAG, "${document.id} => ${document.data["trip_name"]}")
                        tripId = document.id
                        tripName = document.data["trip_name"].toString()
                        tripCoordinates = document.data["coordinates"] as GeoPoint
                        val tripDetail = TripDetails(tripId, tripName, tripCoordinates)
                        tripsList.add(tripDetail)
                    }

                    Log.w(TAG, "Retrieved trips from Firebase")

                    displayTrips()

                }
                .addOnFailureListener{
                    Log.w(TAG, "Error getting documents.")
                    Toast.makeText(applicationContext, "Failure", Toast.LENGTH_LONG).show()
                }
        }else{
            //use singleton to display trips after retrieved from Firebase
            for(trip in tripsList){
                tripId = trip.id
                tripName = trip.name
                tripCoordinates = trip.coordinates
            }
            Log.w(TAG, "Retrieved trips from Singleton")

            displayTrips()
        }


    }//end retrieveTrips()



    private fun displayTrips() {

        tripsRecyclerView.layoutManager = LinearLayoutManager(this)

        //custom adapter for TripDetails data class
        var adapter = TripAdapter(tripsList)
        tripsRecyclerView.adapter = adapter
        adapter.setOnItemClickListener(object: TripAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                //Toast.makeText(this@TripsActivity, "You clicked on ${tripsList[position].trip_name}", Toast.LENGTH_SHORT).show()
                val tripId = tripsList[position].id
                val tripName = tripsList[position].name
                val coordinates = tripsList[position].coordinates
                Log.w(TAG, "tripId: $tripId, tripName: $tripName, coordinates: $coordinates")

                val intent =Intent(this@TripsActivity, PlacesActivity::class.java)
                intent.putExtra("tripId", tripId)//used for Firebase Document Reference
                intent.putExtra("tripLatitude", coordinates.latitude)
                intent.putExtra("tripLongitude", coordinates.longitude)
                startActivity(intent)
            }

        })

        val dividerItemDecoration: DividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        tripsRecyclerView.addItemDecoration(dividerItemDecoration)

    }//end displayTrips()



}//end class