package com.example.travelplanner

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.cardview.widget.CardView
import com.example.travelplanner.databinding.ActivityTestPlacesBinding
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.OpeningHours
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class TestPlacesActivity : DrawerBaseActivity() {

    private lateinit var binding: ActivityTestPlacesBinding

    //init fragments
    private lateinit var placesListFragment: PlacesListFragment
    private lateinit var placesMapFragment: MapFragment

    //Firebase
    private lateinit var fStore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUserId: String
    lateinit var tripsReference: DocumentReference

    //Values From TripsActivity
    private lateinit var tripId: String
    private lateinit var tripName: String
    var tripLatitude: Double = 0.0
    var tripLongitude: Double = 0.0

    //init place variables
    private lateinit var placeName: String
    private lateinit var placeId: String
    private lateinit var placeCoordinates: GeoPoint
    private lateinit var placeTypesArray: ArrayList<String>
    private lateinit var placeAddress: String
    private lateinit var placeOpeningHours: OpeningHours
    private lateinit var placeDetail: PlaceDetails
    lateinit var placeDetailsArray: ArrayList<PlaceDetails>

    //buttons fo switching fragment view
    private lateinit var btnPlacesList: Button
    private lateinit var btnPlacesMap: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestPlacesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //received from TripsActivity
        tripId = intent.getStringExtra("tripId") as String
        tripLatitude = intent.getDoubleExtra("tripLatitude", 0.0)
        tripLongitude = intent.getDoubleExtra("tripLongitude", 0.0)

        //init Firebase
        auth = Firebase.auth
        fStore = Firebase.firestore
        currentUserId = auth.currentUser?.uid.toString()
        tripsReference = fStore.collection("users")
            .document(currentUserId)
            .collection("trips")
            .document(tripId)

        placeDetailsArray = arrayListOf<PlaceDetails>()

        retrievePlaces()


    }//end onCreate()


    private fun retrievePlaces() {
        tripsReference.collection("places").get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    placeName = document.data["name"].toString()
                    placeId = document.id
                    placeAddress = document.data["address"].toString()
                    placeTypesArray = ArrayList()
                    val arrayTypes = document.data["types"] as ArrayList<*>
                    for (type in arrayTypes) {
                        placeTypesArray.add(type.toString())
                    }
                    placeCoordinates = document.data["coordinates"] as GeoPoint
                    placeDetail = PlaceDetails(
                        placeName,
                        placeId,
                        placeCoordinates,
                        placeTypesArray,
                        placeAddress
                    )

                    placeDetailsArray.add(placeDetail)

                }
                initFragments()
            }

    }//end retrievePlaces()


    private fun initFragments() {

        Log.d(ContentValues.TAG, "Test Place Activity: ${placeDetailsArray}");

        //init fragments
        placesListFragment = PlacesListFragment()
        placesMapFragment = MapFragment()

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, placesListFragment)
            commit()
        }

        btnPlacesList = findViewById(R.id.btnPlacesList)
        btnPlacesMap = findViewById(R.id.btnPlacesMap)

        btnPlacesList.setOnClickListener{
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.flFragment, placesListFragment)
                commit()
            }
        }

        btnPlacesMap.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.flFragment, placesMapFragment)
                commit()
            }
        }
    }//end initFragments()






}//end class