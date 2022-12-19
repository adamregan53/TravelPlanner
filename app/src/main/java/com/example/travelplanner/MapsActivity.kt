package com.example.travelplanner

import android.Manifest
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
import androidx.core.content.ContextCompat
import com.example.travelplanner.BuildConfig.MAPS_API_KEY
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.example.travelplanner.databinding.ActivityMapsBinding
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteFragment
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.JsonParser
import org.w3c.dom.Text

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, OnRequestPermissionsResultCallback {

    //Google Maps API
    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    //Values From TripsActivity
    private lateinit var tripId: String
    private lateinit var tripName: String
    private var tripLatitude: Double = 0.0
    private var tripLongitude: Double = 0.0

    //Firebase
    private lateinit var fStore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUserId: String
    private lateinit var tripsReference: DocumentReference

    //
    private lateinit var placeName: String
    private lateinit var placeId: String
    private lateinit var placeCoordinates: GeoPoint
    private lateinit var placeTypesArray: ArrayList<String>
    private lateinit var placeAddress: String
    private lateinit var placeDetailsArray: ArrayList<PlaceDetails>

    private lateinit var addBtn: Button
    private lateinit var cancelBtn: Button

    private lateinit var placeInfoLayout: CardView
    private lateinit var clearPlaceBtn: Button
    private lateinit var listPlaceBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tripId = intent.getStringExtra("tripId") as String
        tripName = intent.getStringExtra("tripName") as String
        tripLatitude = intent.getDoubleExtra("latitude", 0.0)
        tripLongitude= intent.getDoubleExtra("longitude", 0.0)

        placeDetailsArray = ArrayList()

        fStore = Firebase.firestore
        auth = Firebase.auth
        currentUserId = auth.currentUser?.uid.toString()
        tripsReference = fStore.collection("users").document(currentUserId).collection("trips").document(tripId)

        //init buttons
        addBtn = findViewById(R.id.addBtn)
        cancelBtn = findViewById(R.id.cancelBtn)
        addBtn.visibility = View.INVISIBLE
        cancelBtn.visibility = View.INVISIBLE

        //placeInfoCard
        placeInfoLayout = findViewById(R.id.place_details_card)
        placeInfoLayout.visibility = View.INVISIBLE
        clearPlaceBtn = findViewById(R.id.cardbtn_clear)
        listPlaceBtn = findViewById(R.id.list_places_btn)

        //Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        initPlacesAutocomplete()


    }

    private fun initPlacesAutocomplete() {
        if(!Places.isInitialized()){
            Places.initialize(applicationContext, MAPS_API_KEY)
        }

        val placesClient: PlacesClient = Places.createClient(this)

        val autoCompleteFragment = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        autoCompleteFragment.setLocationBias(RectangularBounds.newInstance(
            LatLng(tripLatitude,tripLongitude),
            LatLng(tripLatitude,tripLongitude)

        ))

        autoCompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.TYPES, Place.Field.ADDRESS, Place.Field.OPENING_HOURS))

        autoCompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                Log.i(TAG, "Place: ${place.name}, ${place.id}, ${place.latLng}, ${place.types}, ${place.address}")
                Log.i(TAG, "Json: $place")

                placeName = place.name
                placeId = place.id
                placeCoordinates = GeoPoint(place.latLng.latitude, place.latLng.longitude)
                placeTypesArray= ArrayList()
                for(type in place.types){
                    placeTypesArray.add(type.toString())
                }
                placeAddress = Place.Field.ADDRESS.name
                val placeDetail: PlaceDetails = PlaceDetails(placeName, placeId, placeCoordinates, placeTypesArray, placeAddress)

                val placeLocation = LatLng(placeCoordinates.latitude, placeCoordinates.longitude)
                var newPlaceMarker: Marker? = null
                newPlaceMarker = map.addMarker(
                    MarkerOptions()
                        .position(placeLocation)
                        .title(placeName)
                )



                addBtn.visibility = View.VISIBLE
                cancelBtn.visibility = View.VISIBLE

                addBtn.setOnClickListener{
                    tripsReference.collection("places").add(placeDetail)
                        .addOnSuccessListener{
                            Log.d(TAG, "Place Added Successfully")
                            Toast.makeText(applicationContext, "Place Added Successfully", Toast.LENGTH_SHORT).show()
                            addBtn.visibility = View.INVISIBLE
                            cancelBtn.visibility = View.INVISIBLE
                        }
                        .addOnFailureListener{
                            Log.d(TAG, "Failed to add place")
                            Toast.makeText(applicationContext, "Failed to add place", Toast.LENGTH_SHORT).show()
                            addBtn.visibility = View.INVISIBLE
                            cancelBtn.visibility = View.INVISIBLE
                        }
                }

                cancelBtn.setOnClickListener{
                    newPlaceMarker?.remove()
                    Toast.makeText(applicationContext, "Cancelled", Toast.LENGTH_SHORT).show()
                    addBtn.visibility = View.INVISIBLE
                    cancelBtn.visibility = View.INVISIBLE
                }

            }

            override fun onError(status: Status) {
                Log.i(TAG, "An error occurred: $status")
            }
        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        val tripLocation = LatLng(tripLatitude, tripLongitude)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tripLocation, 12f))

        displayPlaceMarkers()

        map.setOnMarkerClickListener{marker ->
            val markerLocation: LatLng = LatLng(marker.position.latitude, marker.position.longitude)
            val location: CameraUpdate = CameraUpdateFactory.newLatLngZoom(markerLocation, 16f)
            map.animateCamera(location)

            val placeName: TextView = findViewById(R.id.card_place_name)
            val placeAddress: TextView = findViewById(R.id.card_place_address)


            for(place in placeDetailsArray){
                if(place.name == marker.title){
                    placeName.text = place.name.toString()
                    placeAddress.text = place.address.toString()
                }
            }
            placeInfoLayout.visibility = View.VISIBLE

            true
        }

        clearPlaceBtn.setOnClickListener{
            placeInfoLayout.visibility = View.INVISIBLE
        }

        listPlaceBtn.setOnClickListener{

        }

    }


    private fun displayPlaceMarkers() {
        tripsReference.collection("places").get()
            .addOnSuccessListener {result ->
                for(document in result){
                    placeName = document.data["name"].toString()
                    placeId = document.id
                    placeAddress = document.data["address"].toString()
                    placeTypesArray = ArrayList()
                    for(type in placeTypesArray){
                        placeTypesArray.add(type)
                    }
                    placeCoordinates = document.data["coordinates"] as GeoPoint
                    val placeDetail: PlaceDetails = PlaceDetails(placeName, placeId, placeCoordinates, placeTypesArray, placeAddress)
                    placeDetailsArray.add(placeDetail)
                }

                for(place in placeDetailsArray){
                    val placeLocation = LatLng(place.coordinates.latitude, place.coordinates.longitude)
                    map.addMarker(
                        MarkerOptions()
                            .position(placeLocation)
                            .title(place.name)
                    )
                }

            }
            .addOnFailureListener{
                Toast.makeText(applicationContext, "Could not display map markers", Toast.LENGTH_SHORT).show()
            }

    }

}
