package com.example.travelplanner.activities

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.travelplanner.R
import com.example.travelplanner.data.SharedData
import com.example.travelplanner.data.TripDetails
import com.example.travelplanner.databinding.ActivityTripsBinding
import com.example.travelplanner.fragments.TestExpandingList
import com.example.travelplanner.fragments.TripsListFragment
import com.google.firebase.Timestamp
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

    //trip data
    private lateinit var tripId: String
    private lateinit var tripName: String
    private lateinit var tripCoordinates: GeoPoint
    private lateinit var startDate: Timestamp
    private lateinit var endDate: Timestamp
    lateinit var tripsList: ArrayList<TripDetails>

    //layout

    private lateinit var activityTripsBinding: ActivityTripsBinding

    //init fragments
    private lateinit var tripsListFragment: TripsListFragment



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityTripsBinding = ActivityTripsBinding.inflate(layoutInflater)
        allocationActivityTitle("Trips")
        setContentView(activityTripsBinding.root)

        auth = Firebase.auth
        fStore = Firebase.firestore
        currentUserId = auth.currentUser?.uid.toString()


        //tripsList = arrayListOf<TripDetails>()
        tripsList = SharedData.tripsList


        retrieveTrips()

    }//end onCreate()



    private fun retrieveTrips() {
        if(SharedData.tripsList.isEmpty()){
            //reference to user document in database
            Log.d(TAG, "tripsList isEmpty: ${SharedData.tripsList.isEmpty()}");

            val userReference: DocumentReference = fStore.collection("users").document(currentUserId)
            userReference.collection("trips").get()
                .addOnSuccessListener { result ->
                    //get data from documents in trips collection
                    for(document in result){
                        Log.d(TAG, "${document.id} => ${document.data["trip_name"]}")
                        tripId = document.id
                        tripName = document.data["trip_name"].toString()
                        tripCoordinates = document.data["coordinates"] as GeoPoint
                        startDate = document.data["startDate"] as Timestamp
                        endDate = document.data["endDate"] as Timestamp
                        val tripDetail = TripDetails(tripId, tripName, tripCoordinates, startDate, endDate)
                        tripsList.add(tripDetail)
                    }

                    Log.w(TAG, "Retrieved trips from Firebase")

                    initFragments()

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
                startDate = trip.startDate
                endDate = trip.endDate
            }
            Log.w(TAG, "Retrieved trips from Singleton")

            initFragments()
        }


    }//end retrieveTrips()



    private fun initFragments() {
        tripsListFragment = TripsListFragment()
        val testExpandingList: TestExpandingList = TestExpandingList()

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.tripsFlFragment, tripsListFragment)
            commit()
        }


    }//end initFragments()


}//end class