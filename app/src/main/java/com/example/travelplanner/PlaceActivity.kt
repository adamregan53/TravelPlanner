package com.example.travelplanner

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travelplanner.databinding.ActivityPlaceBinding
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PlaceActivity : AppCompatActivity() {

    private lateinit var tripId: String

    //Firestore
    private lateinit var fStore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUserId: String
    private lateinit var tripsReference: DocumentReference

    //recycler view
    private lateinit var placeRecyclerView: RecyclerView

    //place details
    private lateinit var placeName: String
    private lateinit var placeId: String
    private lateinit var placeCoordinates: GeoPoint
    private lateinit var placeTypesArray: ArrayList<String>
    private lateinit var placeAddress: String
    private lateinit var placeDetail: PlaceDetails
    lateinit var placeDetailsArray: ArrayList<PlaceDetails>

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place)

        tripId = intent.getStringExtra("tripId") as String

        auth = Firebase.auth
        fStore = Firebase.firestore
        currentUserId = auth.currentUser?.uid.toString()
        tripsReference = fStore.collection("users")
            .document(currentUserId)
            .collection("trips")
            .document(tripId)

        placeRecyclerView = findViewById(R.id.placeRecyclerView)

        placeDetailsArray = arrayListOf<PlaceDetails>()

        val singleton: Singleton = Singleton
        var tripIdInt: Int = 0
        for ((i, trip) in singleton.tripsList.withIndex()){
            if (trip.id == tripId){
                tripIdInt = i
            }
        }
        Log.d(ContentValues.TAG, "Singleton Test: ${singleton.tripsList[tripIdInt].name}");


        retrievePlaces()


    }//end onCreate()

    private fun retrievePlaces(){
        tripsReference.collection("places").get()
            .addOnSuccessListener {result ->
                for(document in result){
                    placeName = document.data["name"].toString()
                    placeId = document.id
                    placeAddress = document.data["address"].toString()
                    placeTypesArray = ArrayList()
                    val arrayTypes = document.data["types"] as ArrayList<*>
                    for(type in arrayTypes){
                        placeTypesArray.add(type.toString())
                    }
                    placeCoordinates = document.data["coordinates"] as GeoPoint
                    placeDetail = PlaceDetails(placeName, placeId, placeCoordinates, placeTypesArray, placeAddress)
                    placeDetailsArray.add(placeDetail)
                }//end for()
                for(place in placeDetailsArray){
                    Log.w(ContentValues.TAG, "placeId: ${place.id}, placeName: ${place.name}, placeAddress: ${place.address}, placeTypes: ${place.types[0]}, coordinates: ${place.coordinates}")

                }
                displayPlaces()
            }//end addOnSuccessListener()
    }

    private fun displayPlaces(){
        Log.d(ContentValues.TAG, "displayPlaces: array: ${placeDetailsArray}");
        placeRecyclerView.layoutManager = LinearLayoutManager(this)

        //custom adapter for TripDetails data class
        var adapter = PlaceAdapter(placeDetailsArray)
        placeRecyclerView.adapter = adapter
        adapter.setOnItemClickListener(object: PlaceAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {

            }

        })
    }//end displayPlaces()

}//end class
