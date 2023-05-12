package com.example.travelplanner.fragments

import android.content.ContentValues
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import com.example.travelplanner.BuildConfig
import com.example.travelplanner.data.PlaceDetails
import com.example.travelplanner.R
import com.example.travelplanner.activities.PlacesActivity

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


class PlacesMapFragment() : Fragment(), OnMapReadyCallback,
    ActivityCompat.OnRequestPermissionsResultCallback {

    //google maps api
    private lateinit var map:GoogleMap
    private lateinit var mapView: MapView

    //firebase
    private lateinit var fStore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var tripsReference: DocumentReference
    private lateinit var locationsReference: DocumentReference

    private lateinit var placesActivity: PlacesActivity
    private lateinit var placesListFragment: PlacesListFragment
    private var tripLatitude: Double = 0.0
    private var tripLongitude: Double = 0.0

    private lateinit var placeDetailsArray: ArrayList<PlaceDetails>

    //view components
    private lateinit var addBtn: Button//add place button from places autocomplete
    private lateinit var cancelBtn: Button//cancel add place operation

    //place info card
    private lateinit var placeInfoLayout: CardView //main info layout
    private lateinit var expandableDetailsLayout: LinearLayout //expandable info layout
    private lateinit var expandablePlaceCardBtn: Button //expand layout button
    private lateinit var placeInfoName: TextView //display place name on info card
    private lateinit var closeDetailsBtn: View //close info card
    private lateinit var placeInfoTypes: TextView //display place types on info card
    private lateinit var placeInfoAddress: TextView //display place address on info card
    private lateinit var placeOpeningHoursMonday: TextView
    private lateinit var placeOpeningHoursTuesday: TextView
    private lateinit var placeOpeningHoursWednesday: TextView
    private lateinit var placeOpeningHoursThursday: TextView
    private lateinit var placeOpeningHoursFriday: TextView
    private lateinit var placeOpeningHoursSaturday: TextView
    private lateinit var placeOpeningHoursSunday: TextView
    private lateinit var placeInfoRating: TextView
    private lateinit var placeInfoTotalRating: TextView


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //retrieve data from Activity
        placesActivity = activity as PlacesActivity
        placesListFragment = PlacesListFragment()
        tripsReference = placesActivity.tripsReference
        locationsReference = placesActivity.locationsReference
        tripLatitude = placesActivity.tripLatitude
        tripLongitude = placesActivity.tripLongitude

        auth = Firebase.auth
        fStore = Firebase.firestore

        placeDetailsArray = placesActivity.placeDetailsArray

        initPlacesAutocomplete()

    }//end onActivityCreated()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_places_map, container, false)
    }//end onCreateView()



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //google maps
        mapView = view.findViewById(R.id.map_view)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        //init places autocomplete buttons
        addBtn = view.findViewById(R.id.addBtn)
        cancelBtn = view.findViewById(R.id.cancelBtn)
        addBtn.visibility = View.INVISIBLE
        cancelBtn.visibility = View.INVISIBLE

        //placeInfoCard
        placeInfoLayout = view.findViewById(R.id.place_info_card)
        placeInfoLayout.visibility = View.GONE
        expandableDetailsLayout = view.findViewById(R.id.expandable_details_layout)
        expandablePlaceCardBtn = view.findViewById(R.id.expand_place_card_btn)
        expandablePlaceCardBtn.setOnClickListener{
            if(expandableDetailsLayout.visibility == View.GONE){
                expandInfoCard()
            }else{
                collapseInfoCard()
            }
        }
        placeInfoName = view.findViewById(R.id.place_name)
        closeDetailsBtn = view.findViewById(R.id.close_details_btn)
        closeDetailsBtn.setOnClickListener{
            closeInfoCard()
        }
        placeInfoTypes = view.findViewById(R.id.place_types)
        placeInfoAddress = view.findViewById(R.id.place_address)
        placeOpeningHoursMonday = view.findViewById(R.id.place_opening_hours_monday_txt)
        placeOpeningHoursTuesday = view.findViewById(R.id.place_opening_hours_tuesday_txt)
        placeOpeningHoursWednesday = view.findViewById(R.id.place_opening_hours_wednesday_txt)
        placeOpeningHoursThursday = view.findViewById(R.id.place_opening_hours_thursday_txt)
        placeOpeningHoursFriday = view.findViewById(R.id.place_opening_hours_friday_txt)
        placeOpeningHoursSaturday = view.findViewById(R.id.place_opening_hours_saturday_txt)
        placeOpeningHoursSunday = view.findViewById(R.id.place_opening_hours_sunday_txt)
        placeInfoRating = view.findViewById(R.id.place_rating)
        placeInfoTotalRating = view.findViewById(R.id.place_total_rating)

    }//end onViewCreated()

    private fun collapseInfoCard() {
        TransitionManager.beginDelayedTransition(placeInfoLayout, AutoTransition())
        expandableDetailsLayout.visibility = View.GONE
    }//end closeInfoCard()

    private fun expandInfoCard() {
        TransitionManager.beginDelayedTransition(placeInfoLayout, AutoTransition())
        expandableDetailsLayout.visibility = View.VISIBLE
    }//end openInfoCard()

    private fun closeInfoCard(){
        TransitionManager.beginDelayedTransition(placeInfoLayout, AutoTransition())
        placeInfoLayout.visibility = View.GONE
    }


    override fun onStart() {
        super.onStart()
        Log.d(ContentValues.TAG, "Map Fragment, onStart() called");
        mapView?.onStart()
    }//end onStart()

    override fun onStop() {
        super.onStop()
        Log.d(ContentValues.TAG, "Map Fragment, onStop() called");
        mapView?.onStop()
    }//end onStop()

    override fun onResume() {
        super.onResume()
        Log.d(ContentValues.TAG, "Map Fragment, onResume() called");
        mapView?.onResume()

    }//end onResume()

    override fun onLowMemory() {
        super.onLowMemory()
        Log.d(ContentValues.TAG, "Map Fragment, onLowMemory() called");
        mapView?.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(ContentValues.TAG, "Map Fragment, onSavedInstanceState() called");
        mapView?.onSaveInstanceState(outState)
    }

    override fun onPause() {
        super.onPause()
        Log.d(ContentValues.TAG, "Map Fragment, onPause() called");
        mapView?.onPause()

        placeInfoLayout.visibility = View.INVISIBLE
    }//end onPause()


    override fun onMapReady(googleMap: GoogleMap) {
        Log.d(ContentValues.TAG, "Map Fragment, onMapReady() called");
        map = googleMap

        //set camera to trip location
        val tripLocation = LatLng(tripLatitude, tripLongitude)

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(tripLocation, 12f))

        displayPlaceMarkers()

        map.setOnMarkerClickListener{marker ->
            val markerLocation: LatLng = LatLng(marker.position.latitude, marker.position.longitude)
            val location: CameraUpdate = CameraUpdateFactory.newLatLngZoom(markerLocation, 16f)
            map.animateCamera(location)

            placeOpeningHoursMonday.text = ""
            placeOpeningHoursTuesday.text = ""
            placeOpeningHoursWednesday.text = ""
            placeOpeningHoursThursday.text = ""
            placeOpeningHoursFriday.text = ""
            placeOpeningHoursSaturday.text = ""
            placeOpeningHoursSunday.text = ""
            placeInfoRating.text = ""
            placeInfoTotalRating.text = ""

            for(place in placeDetailsArray){
                if(place.name == marker.title){
                    placeInfoName.text = place.name.toString()
                    placeInfoTypes.text = place.types.toString()
                    placeInfoAddress.text = place.address
                    if(place.openingHoursText!!.isNotEmpty()) {
                        placeOpeningHoursMonday.text = place.openingHoursText[0]
                        placeOpeningHoursTuesday.text = place.openingHoursText[1]
                        placeOpeningHoursWednesday.text = place.openingHoursText[2]
                        placeOpeningHoursThursday.text = place.openingHoursText[3]
                        placeOpeningHoursFriday.text = place.openingHoursText[4]
                        placeOpeningHoursSaturday.text = place.openingHoursText[5]
                        placeOpeningHoursSunday.text = place.openingHoursText[6]
                    }
                    if(place.rating != null){
                        placeInfoRating.text = place.rating.toString()
                    }
                    if(place.totalRatings != null) {
                        placeInfoTotalRating.text = "(${place.totalRatings.toString()})"
                    }
                }
            }
            TransitionManager.beginDelayedTransition(placeInfoLayout, AutoTransition())
            placeInfoLayout.visibility = View.VISIBLE

            true
        }

    }//end onMapReady()


    private fun displayPlaceMarkers() {
        Log.d(ContentValues.TAG, "Map Fragment: ${placeDetailsArray}");

        for(place in placeDetailsArray){
            val placeLocation = LatLng(place.coordinates.latitude, place.coordinates.longitude)
            map.addMarker(
                MarkerOptions()
                    .position(placeLocation)
                    .title(place.name)
            )
        }
    }//end displayMarkers()


    private fun initPlacesAutocomplete() {
        if(!Places.isInitialized()){
            Places.initialize(this.placesActivity, BuildConfig.MAPS_API_KEY)
        }

        val placesClient: PlacesClient = Places.createClient(this.placesActivity)

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
            Place.Field.ADDRESS_COMPONENTS,
            Place.Field.OPENING_HOURS,
            Place.Field.RATING,
            Place.Field.USER_RATINGS_TOTAL)
        )

        var placeName: String
        var placeId: String
        var placeCoordinates: GeoPoint
        var placeTypesArray: ArrayList<String>
        var placeAddress: String
        var placeOpeningHoursArray: ArrayList<Any>
        var placeOpeningHoursTextArray: ArrayList<String>
        var placeRating: Double?
        var placeTotalRatings: Int?

        var placeDetailsMap = hashMapOf<Any, Any>()

        autoCompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {

            override fun onPlaceSelected(place: Place) {
                Log.i("Places", "Place: ${place.name}, ${place.id}, ${place.latLng}, ${place.types}, ${place.address}, ${place.openingHours}")
                Log.i("Places", "Json: $place")

                //set info layout to visible
                placeInfoLayout.visibility = View.INVISIBLE

                //build place details map
                placeName = place.name.toString()
                placeDetailsMap["name"] = placeName

                placeId = place.id.toString()
                placeDetailsMap["id"] = placeId

                placeCoordinates = GeoPoint(place.latLng.latitude, place.latLng.longitude)
                placeDetailsMap["coordinates"] = placeCoordinates

                placeTypesArray= ArrayList()
                for(type in place.types){
                    placeTypesArray.add(type.toString())
                }
                placeDetailsMap["types"] = placeTypesArray

                placeAddress = place.address.toString()
                placeDetailsMap["address"] = placeAddress

                val placeOpeningHours: OpeningHours? = place.openingHours
                placeOpeningHoursArray = ArrayList<Any>()
                if (placeOpeningHours != null) {
                    for(period in placeOpeningHours.periods){
                        val openingHours = hashMapOf(
                            "openDay" to period.open.day.name,
                            "closeDay" to period.close.day.name,
                            "openHours" to period.open.time.hours,
                            "openMinutes" to period.open.time.minutes,
                            "closeHours" to period.close.time.hours,
                            "closeMinutes" to period.close.time.minutes
                        )
                        placeOpeningHoursArray.add(openingHours)
                    }
                    placeDetailsMap["openingHours"] = placeOpeningHoursArray
                }

                val placeOpeningHoursText: OpeningHours? = place.openingHours
                placeOpeningHoursTextArray = ArrayList()
                if (placeOpeningHoursText != null) {
                    for (text in placeOpeningHoursText.weekdayText) {
                        placeOpeningHoursTextArray.add(text.toString())
                    }
                    placeDetailsMap["openingHoursText"] = placeOpeningHoursTextArray
                }

                placeRating = place.rating
                if(placeRating != null) {
                    placeDetailsMap["rating"] = placeRating as Double
                }

                placeTotalRatings = place.userRatingsTotal
                if(placeTotalRatings != null) {
                    placeDetailsMap["totalRatings"] = placeTotalRatings as Int
                }


                val placeDetail = PlaceDetails(placeName, placeId, placeCoordinates, placeTypesArray, placeAddress, placeOpeningHoursArray, placeOpeningHoursTextArray, placeRating, placeTotalRatings)
                Log.i(ContentValues.TAG, "Place Detail: $placeDetail")

                Log.i(ContentValues.TAG, "Place Detail Map: $placeDetailsMap")

                //set marker and animate camera
                val placeLocation = LatLng(placeCoordinates.latitude, placeCoordinates.longitude)
                var newPlaceMarker: Marker? = null
                newPlaceMarker = map.addMarker(
                    MarkerOptions()
                        .position(placeLocation)
                        .title(placeName)
                )
                val placeLatLng: LatLng = LatLng(placeCoordinates.latitude, placeCoordinates.longitude)
                val location: CameraUpdate = CameraUpdateFactory.newLatLngZoom(placeLatLng, 16f)
                map.animateCamera(location)

                addBtn.visibility = View.VISIBLE
                cancelBtn.visibility = View.VISIBLE

                addBtn.setOnClickListener{
                    tripsReference.collection("places").add(placeDetailsMap)
                        .addOnSuccessListener{
                            Log.d(ContentValues.TAG, "Place Added Successfully")
                            Toast.makeText(context, "Place Added Successfully", Toast.LENGTH_SHORT).show()
                            placeDetailsArray.add(placeDetail)
                            addBtn.visibility = View.INVISIBLE
                            cancelBtn.visibility = View.INVISIBLE

                            locationsReference.collection("places").add(placeDetailsMap)
                                .addOnSuccessListener {
                                    Log.d(ContentValues.TAG, "Place Added To Locations")
                                }//end addOnSuccessListener()
                                .addOnFailureListener{
                                    Log.d(ContentValues.TAG, "Place Failed to add to Locations")
                                }//end addOnFailureListener

                            //add to locations collection
                            /*
                            locationsReference.collection("places").whereEqualTo("id", placeId).get()
                                .addOnSuccessListener { result ->
                                    //if there is no matching place
                                    val locationsMap = placeDetailsMap
                                    var id: String = ""
                                    var userSearches: Int = 0

                                    if(result.isEmpty){
                                        userSearches = 1
                                        locationsMap["userSearches"] = userSearches
                                        locationsReference.collection("places").add(locationsMap)
                                            .addOnSuccessListener {
                                                Log.d(ContentValues.TAG, "Place Added To Locations")
                                            }//end addOnSuccessListener()
                                            .addOnFailureListener{
                                                Log.d(ContentValues.TAG, "Place Failed to add to Locations")
                                            }//end addOnFailureListener
                                    }else{
                                        for(doc in result){
                                            if(result.size() <= 1){
                                                id = doc.id
                                                userSearches = doc.data["userSearches"] as Int
                                                Log.d(ContentValues.TAG, "Location Doc ID: $id")

                                            }else{
                                                Log.d(ContentValues.TAG, "List of locations is greater than 1")
                                            }
                                        }
                                        userSearches += 1
                                        Log.d(ContentValues.TAG, "User Searches after add: $userSearches")
                                        if(id.isNotEmpty()){
                                            locationsReference.collection("places").document(id).update("userSearches", userSearches)
                                                .addOnSuccessListener {
                                                    Log.d(ContentValues.TAG, "Location userSearches Updated")
                                                }
                                                .addOnFailureListener{
                                                    Log.d(ContentValues.TAG, "Location Failed to Update")

                                                }
                                        }else{
                                            Log.d(ContentValues.TAG, "location id is empty")
                                        }

                                    }
                                }
                                .addOnFailureListener{
                                    Log.d(ContentValues.TAG, "Failed to get locations")

                                }
                                */

                        }//end addOnSuccessListener()
                        .addOnFailureListener{
                            Log.d(ContentValues.TAG, "Failed to add place")
                            Toast.makeText(context, "Failed to add place", Toast.LENGTH_SHORT).show()
                            addBtn.visibility = View.INVISIBLE
                            cancelBtn.visibility = View.INVISIBLE
                        }

                }//end onClickListener()

                cancelBtn.setOnClickListener{
                    newPlaceMarker?.remove()
                    Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show()
                    addBtn.visibility = View.INVISIBLE
                    cancelBtn.visibility = View.INVISIBLE
                }//end onClickListener

            }//end onPlaceSelected()

            override fun onError(status: Status) {
                Log.i(ContentValues.TAG, "Could not find place: $status")

            }//end onError()

        })//end setOnPlaceSelectedListener()

    }//end initAutocompletePlaces()



}//end class