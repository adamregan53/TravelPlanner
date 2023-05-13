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
import com.example.travelplanner.fragments.PlacesItineraryFragment
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
    lateinit var newTripId: String
    lateinit var newTripName: String
    lateinit var newTripCoordinates: GeoPoint
    lateinit var newStartDate: Timestamp
    lateinit var newEndDate: Timestamp
    lateinit var newLocationId: String
    lateinit var newLocationRef: String
    var newUtcOffset: Int = 0
    var isNewItineraryGenerated: Boolean = false

    //layout
    private lateinit var activityTripsBinding: ActivityTripsBinding

    //init fragments
    private lateinit var tripsListFragment: TripsListFragment
    private lateinit var tripsItineraryFragment: PlacesItineraryFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityTripsBinding = ActivityTripsBinding.inflate(layoutInflater)
        allocationActivityTitle("Trips")
        setContentView(activityTripsBinding.root)

        auth = Firebase.auth
        fStore = Firebase.firestore
        currentUserId = auth.currentUser?.uid.toString()

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
                    var tripId: String
                    var tripAddress: String
                    var tripCoordinates: GeoPoint
                    var endDate: Timestamp
                    var isItineraryGenerated: Boolean = false
                    var locationId: String
                    var locationRef: String
                    var tripName: String
                    var startDate: Timestamp
                    var typesArray: ArrayList<String>
                    var utcOffset: Int

                    for(document in result){
                        Log.d(TAG, "${document.id} => ${document.data["name"]}")
                        tripId = document.id
                        tripAddress = document.data["address"].toString()
                        tripCoordinates = document.data["coordinates"] as GeoPoint
                        endDate = document.data["endDate"] as Timestamp
                        isItineraryGenerated = document.data["isItineraryGenerated"]  as Boolean
                        locationId = document.data["locationId"].toString()
                        locationRef = document.data["locationRef"].toString()
                        tripName = document.data["name"].toString()
                        startDate = document.data["startDate"] as Timestamp
                        val types = document.data["types"] as ArrayList<*>
                        typesArray = ArrayList()
                        for(type in types){
                            typesArray.add(type.toString())
                        }
                        val utcOffsetLong = document.data["utcOffset"] as Long
                        utcOffset = utcOffsetLong.toInt()

                        val tripDetail = TripDetails(
                            tripId,
                            tripAddress,
                            tripCoordinates,
                            endDate,
                            isItineraryGenerated,
                            locationId,
                            locationRef,
                            tripName,
                            startDate,
                            typesArray,
                            utcOffset)
                        SharedData.tripsList.add(tripDetail)
                    }

                    Log.w(TAG, "Retrieved trips from Firebase")

                    initFragments()

                }
                .addOnFailureListener{
                    Log.w(TAG, "Error getting documents.")
                    Toast.makeText(applicationContext, "Failure", Toast.LENGTH_LONG).show()
                }
        }else{
            Log.w(TAG, "Retrieved trips from SharedData")
            initFragments()
        }

    }//end retrieveTrips()



    private fun initFragments() {
        tripsListFragment = TripsListFragment()
        tripsItineraryFragment = PlacesItineraryFragment()
        val testExpandingList: TestExpandingList = TestExpandingList()

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.tripsFlFragment, tripsListFragment)
            commit()
        }

    }//end initFragments()


}//end class