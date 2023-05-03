package com.example.travelplanner

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.view.get

import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.OpeningHours
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class MapFragment : Fragment(), OnMapReadyCallback,
    ActivityCompat.OnRequestPermissionsResultCallback {

    private lateinit var googleMap:GoogleMap
    private lateinit var mapView: MapView

    private lateinit var fStore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var tripsReference: DocumentReference

    private lateinit var testPlacesActivity: TestPlacesActivity
    private var tripLatitude: Double = 0.0
    private var tripLongitude: Double = 0.0

    private lateinit var placeName: String
    private lateinit var placeId: String
    private lateinit var placeCoordinates: GeoPoint
    private lateinit var placeTypesArray: ArrayList<String>
    private lateinit var placeAddress: String
    private lateinit var placeOpeningHours: OpeningHours
    private lateinit var placeDetail: PlaceDetails
    private lateinit var placeDetailsArray: ArrayList<PlaceDetails>

    //view components
    private lateinit var addBtn: Button
    private lateinit var cancelBtn: Button
    private lateinit var placeInfoLayout: CardView
    private lateinit var clearPlaceBtn: Button


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mapView = view?.findViewById<MapView>(R.id.map_view)!!
        mapView.onCreate(savedInstanceState)
        mapView.onResume()

        mapView.getMapAsync(this)

        //retrieve data from Activity
        testPlacesActivity = activity as TestPlacesActivity
        tripsReference = testPlacesActivity.tripsReference
        tripLatitude = testPlacesActivity.tripLatitude
        tripLongitude = testPlacesActivity.tripLongitude

        auth = Firebase.auth
        fStore = Firebase.firestore

        placeDetailsArray = testPlacesActivity.placeDetailsArray

        //init buttons
        addBtn = requireView().findViewById(R.id.addBtn)
        cancelBtn = requireView().findViewById(R.id.cancelBtn)
        addBtn.visibility = View.INVISIBLE
        cancelBtn.visibility = View.INVISIBLE

        //placeInfoCard
        placeInfoLayout = requireView().findViewById(R.id.place_details_card)
        placeInfoLayout.visibility = View.INVISIBLE
        clearPlaceBtn = requireView().findViewById(R.id.cardbtn_clear)

        initPlacesAutocomplete()

    }//end onActivityCreated()

    private fun initPlacesAutocomplete() {
        if(!Places.isInitialized()){
            Places.initialize(this.testPlacesActivity, BuildConfig.MAPS_API_KEY)
        }

        val placesClient: PlacesClient = Places.createClient(this.testPlacesActivity)

        //childFragmentManger includes SupportFragmentManager which allows this to work inside a fragment
        val autoCompleteFragment = childFragmentManager.findFragmentById(R.id.autocomplete_fragment_test) as AutocompleteSupportFragment

        autoCompleteFragment.setLocationBias(
            RectangularBounds.newInstance(
            LatLng(tripLatitude,tripLongitude),
            LatLng(tripLatitude,tripLongitude)

        ))

        //specifies the information the application will receive from the API
        autoCompleteFragment.setPlaceFields(listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.LAT_LNG,
            Place.Field.TYPES,
            Place.Field.ADDRESS,
            Place.Field.OPENING_HOURS,
            Place.Field.RATING,
            Place.Field.WEBSITE_URI,
            Place.Field.UTC_OFFSET)
        )

        autoCompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {

            override fun onPlaceSelected(place: Place) {
                Log.i("Places", "Place: ${place.name}, ${place.id}, ${place.latLng}, ${place.types}, ${place.address}, ${place.openingHours}")
                Log.i("Places", "Json: $place")

                placeInfoLayout.visibility = View.INVISIBLE

                placeName = place.name
                placeId = place.id
                placeCoordinates = GeoPoint(place.latLng.latitude, place.latLng.longitude)
                placeTypesArray= ArrayList()
                for(type in place.types){
                    placeTypesArray.add(type.toString())
                }
                placeAddress = place.address
                placeOpeningHours = place.openingHours

                Log.i("Place Opening Hours", "${placeOpeningHours.periods}")

                val placeOpeningHoursArray = ArrayList<Any>()
                for (period in placeOpeningHours.periods){
                    val openingHours = hashMapOf(
                        "dayOpen" to period.open.day.name,
                        "dayClose" to period.close.day.name,
                        "close" to period.close.time,
                        "open" to period.open.time
                    )
                    placeOpeningHoursArray.add(openingHours)

                }

                for (openingHours in placeOpeningHoursArray){
                    Log.i("Place Opening Hours", "${openingHours}")
                }


                val placeDetail = PlaceDetails(placeName, placeId, placeCoordinates, placeTypesArray, placeAddress)

                //set marker and animate camera
                val placeLocation = LatLng(placeCoordinates.latitude, placeCoordinates.longitude)
                var newPlaceMarker: Marker? = null
                newPlaceMarker = googleMap.addMarker(
                    MarkerOptions()
                        .position(placeLocation)
                        .title(placeName)
                )
                val placeLatLng: LatLng = LatLng(placeCoordinates.latitude, placeCoordinates.longitude)
                val location: CameraUpdate = CameraUpdateFactory.newLatLngZoom(placeLatLng, 16f)
                googleMap.animateCamera(location)

                addBtn.visibility = View.VISIBLE
                cancelBtn.visibility = View.VISIBLE

                addBtn.setOnClickListener{
                    tripsReference.collection("places").add(placeDetail)
                        .addOnSuccessListener{
                            Log.d(ContentValues.TAG, "Place Added Successfully")
                            Toast.makeText(context, "Place Added Successfully", Toast.LENGTH_SHORT).show()
                            placeDetailsArray.add(placeDetail)
                            addBtn.visibility = View.INVISIBLE
                            cancelBtn.visibility = View.INVISIBLE
                        }
                        .addOnFailureListener{
                            Log.d(ContentValues.TAG, "Failed to add place")
                            Toast.makeText(context, "Failed to add place", Toast.LENGTH_SHORT).show()
                            addBtn.visibility = View.INVISIBLE
                            cancelBtn.visibility = View.INVISIBLE
                        }
                    fStore.collection("places").add(placeDetail)
                        .addOnSuccessListener {
                            Log.d(ContentValues.TAG, "Place Added To Full Places List")
                        }
                        .addOnFailureListener{
                            Log.d(ContentValues.TAG, "Place Failed to add to Full List")
                        }
                }

                cancelBtn.setOnClickListener{
                    newPlaceMarker?.remove()
                    Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show()
                    addBtn.visibility = View.INVISIBLE
                    cancelBtn.visibility = View.INVISIBLE
                }
            }

            override fun onError(status: Status) {
                Log.i(ContentValues.TAG, "Could not find place: $status")

            }
        })
    }//end initAutocompletePlaces()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_map, container, false)
    }//end onCreateView()

    override fun onMapReady(map: GoogleMap) {
        //this works so don't touch it
        map?.let {
            googleMap = it

            //set camera to trip location
            val tripLocation = LatLng(tripLatitude, tripLongitude)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(tripLocation, 12f))

            displayPlaceMarkers()

            map.setOnMarkerClickListener{marker ->
                val markerLocation: LatLng = LatLng(marker.position.latitude, marker.position.longitude)
                val location: CameraUpdate = CameraUpdateFactory.newLatLngZoom(markerLocation, 16f)
                map.animateCamera(location)

                val placeName: TextView = requireView().findViewById(R.id.card_place_name)
                val placeAddress: TextView = requireView().findViewById(R.id.card_place_address)


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

        }
    }//end onMapReady()

    private fun displayPlaceMarkers() {
        Log.d(ContentValues.TAG, "Map Fragment: ${placeDetailsArray}");

        for(place in placeDetailsArray){
                val placeLocation = LatLng(place.coordinates.latitude, place.coordinates.longitude)
                googleMap.addMarker(
                    MarkerOptions()
                        .position(placeLocation)
                        .title(place.name)
                )
            }
    }//end displayMarkers()

}//end class