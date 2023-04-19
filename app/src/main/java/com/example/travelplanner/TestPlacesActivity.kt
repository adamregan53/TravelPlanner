package com.example.travelplanner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.cardview.widget.CardView
import com.example.travelplanner.databinding.ActivityTestPlacesBinding
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
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
    private lateinit var placeDetail: PlaceDetails
    private lateinit var placeDetailsArray: ArrayList<PlaceDetails>

    private lateinit var addBtn: Button
    private lateinit var cancelBtn: Button

    private lateinit var placeInfoLayout: CardView
    private lateinit var clearPlaceBtn: Button

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

        initFragments()

    }//end onCreate()


    private fun initFragments() {
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