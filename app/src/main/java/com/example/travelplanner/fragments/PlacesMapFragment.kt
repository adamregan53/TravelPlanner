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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travelplanner.BuildConfig
import com.example.travelplanner.data.PlaceDetails
import com.example.travelplanner.R
import com.example.travelplanner.activities.PlacesActivity
import com.example.travelplanner.adapters.PlaceAdapter
import com.example.travelplanner.adapters.RecommendationAdapter
import com.example.travelplanner.data.api.PostRequest
import com.example.travelplanner.data.api.PostService

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
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


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
    private lateinit var addRecommendBtn: Button
    private lateinit var cancelRecommendBtn: Button

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

    //recommendation
    private lateinit var recommendationView: ConstraintLayout
    private lateinit var recommendationHeader: TextView
    private lateinit var recommendationRecyclerView: RecyclerView
    private lateinit var adapter: RecommendationAdapter
    private lateinit var recommendationButton: Button
    private lateinit var closeRecommendationButton: View
    private val service = PostService.create()
    private lateinit var postRecommendation: PostRequest
    private lateinit var recommendationResponseList: ArrayList<String>
    private lateinit var recommendationPlacesList: ArrayList<PlaceDetails>


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
        recommendationResponseList = ArrayList()
        recommendationPlacesList = arrayListOf()

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
        addRecommendBtn = view.findViewById(R.id.addRecommendBtn)
        cancelRecommendBtn = view.findViewById(R.id.cancelRecommendBtn)
        addRecommendBtn.visibility = View.INVISIBLE
        cancelRecommendBtn.visibility = View.INVISIBLE

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

        //recommendation
        recommendationView = view.findViewById(R.id.recommendation_view)
        recommendationHeader = view.findViewById(R.id.recommendation_header)
        recommendationRecyclerView = view.findViewById(R.id.recommendation_recyclerview)
        recommendationButton = view.findViewById(R.id.recommendation_btn)
        closeRecommendationButton = view.findViewById(R.id.close_recommendations_btn)


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

            var docId: String
            var placeId: String
            var locationRef: String
            var name: String
            var types: ArrayList<String>

            for(place in placeDetailsArray){
                if(place.placeId == marker.title){

                    docId = place.docId.toString()
                    placeId = place.placeId.toString()
                    locationRef = placesActivity.tripLocationRef
                    name = place.name.toString()
                    types = place.types as ArrayList<String>
                    postRecommendation = PostRequest(docId, placeId, locationRef, name, types)
                    Log.d(ContentValues.TAG, "Post Request: $postRecommendation");


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

            recommendationButton.setOnClickListener{
                //reset lists
                recommendationResponseList.clear()
                recommendationPlacesList.clear()

                lifecycleScope.launch(Dispatchers.IO){

                    if (postRecommendation != null){
                        val postResponse = service.createPost(postRecommendation)
                        if (postResponse != null) {
                            for(response in postResponse){
                                recommendationResponseList.add(response.docId)

                                Log.d(ContentValues.TAG, "Post Response: ${response.docId}");

                            }
                            for(id in recommendationResponseList) {
                                Log.d(
                                    ContentValues.TAG,
                                    "RecommendationResponseList: ${id}"
                                );
                            }
                            placesActivity.runOnUiThread {
                                Log.d(ContentValues.TAG, "openRecommendationView() attempting to open");
                                openRecommendationView()

                            }

                            Log.d(ContentValues.TAG, "Attempting to call getRecommendPlaces()");

                        }else{
                            Toast.makeText(context, "No Response from Recommendation API", Toast.LENGTH_SHORT).show()
                        }
                    }
                }//end lifeCycleScope
            }//end setOnClickListener()

            TransitionManager.beginDelayedTransition(placeInfoLayout, AutoTransition())
            placeInfoLayout.visibility = View.VISIBLE

            true
        }//end onMarkerClicked()

    }//end onMapReady()

    private fun openRecommendationView() {
        Log.d(ContentValues.TAG, "openRecommendationView() opened");

        placeInfoLayout.visibility = View.GONE
        TransitionManager.beginDelayedTransition(recommendationView, AutoTransition())
        recommendationView.visibility = View.VISIBLE
        recommendationRecyclerView.visibility = View.VISIBLE
        closeRecommendationButton.setOnClickListener {
            closeRecommendationView()
        }
        getRecommendedPlaces()
    }

    private fun closeRecommendationView(){
        TransitionManager.beginDelayedTransition(recommendationView, AutoTransition())
        recommendationView.visibility = View.GONE
    }

    private  fun getRecommendedPlaces() {
        Log.d(ContentValues.TAG, "getRecommendedPlaces() called");

        for((index, docId) in recommendationResponseList.withIndex()){
            locationsReference.collection("places").document(docId).get()
                .addOnSuccessListener { result ->
                    Log.d(ContentValues.TAG, "addOnSuccessListener: Found Place");
                    val docId = result.id
                    val name = result.data?.get("name").toString()
                    val placeId = result.data?.get("id").toString()
                    val coordinates = result.data?.get("coordinates") as GeoPoint
                    val types = result.data!!.get("types") as ArrayList<*>
                    val typesArray: ArrayList<String> = ArrayList()
                    for(type in types){
                        typesArray.add(type.toString())
                    }
                    val address = result.data!!.get("address").toString()

                    val openingHoursArray: ArrayList<Any> = ArrayList()
                    if(isDocumentNull(result,"openingHours")){
                        val openingHours = result.data!!.get("openingHours") as ArrayList<Any>
                        for(openHours in openingHours) {
                            val map = openHours as HashMap<*, *>
                            openingHoursArray.add(map)
                        }
                    }

                    val openingHoursTextArray: ArrayList<String> = ArrayList()
                    if(isDocumentNull(result, "openingHoursText")){
                        val openingHoursText = result.data!!.get("openingHoursText") as ArrayList<String>
                        for(openingHours in openingHoursText){
                            openingHoursTextArray.add(openingHoursText.toString())
                        }
                    }

                    var rating: Double? = null
                    if(isDocumentNull(result, "rating")){
                        rating = result.data!!.get("rating") as Double
                    }

                    var ratingsTotal: Int? = null
                    if(isDocumentNull(result, "totalRatings")){
                        var ratingsTotalLong = result.data!!.get("totalRatings") as Long
                        ratingsTotal = ratingsTotalLong.toInt()
                    }

                    val placeDetail = PlaceDetails(docId, name, placeId, coordinates, typesArray, address, openingHoursArray, openingHoursTextArray, rating, ratingsTotal)
                    Log.d(ContentValues.TAG, "addOnSuccessListener: placeDetail: $placeDetail");
                    recommendationPlacesList.add(placeDetail)
                    Log.d(ContentValues.TAG, "addOnSuccessListener: recommendationList: $recommendationPlacesList");

                    initRecommendationRecyclerView()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Could Not retrieve recommendations from firebase", Toast.LENGTH_SHORT).show()

                }

        }

    }//end getRecommendedPlaces()



    private fun updateRecommendations() {
        Log.d(ContentValues.TAG, "updateRecommendations() called");
        Log.d(ContentValues.TAG, "Update: recommendationPlacesList -> ${recommendationPlacesList}");

        adapter.notifyDataSetChanged()
        Log.d(ContentValues.TAG, "adapter itemCount -> ${adapter.itemCount}");

        Log.d(ContentValues.TAG, "openRecommendationView() attempting to open");
        openRecommendationView()
    }//end displayRecommendations()

    private fun initRecommendationRecyclerView() {
        Log.d(ContentValues.TAG, "initRecommendationRecyclerView() called");
        recommendationRecyclerView.layoutManager = LinearLayoutManager(this.placesActivity)

        adapter = RecommendationAdapter(recommendationPlacesList)
        recommendationRecyclerView.adapter = adapter
        adapter.setOnItemClickListener(object: RecommendationAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                Log.d(ContentValues.TAG, "recommendationPlacesList position: $position -> ${recommendationPlacesList[position]}");
                locateRecommendation(position)
            }

        })

        val dividerItemDecoration = DividerItemDecoration(this.placesActivity, DividerItemDecoration.VERTICAL)
        recommendationRecyclerView.addItemDecoration(dividerItemDecoration)

        Log.d(ContentValues.TAG, "initRecommendationsRecyclerView(): recommendationPlacesList -> ${recommendationPlacesList}");


    }

    private fun locateRecommendation(position: Int) {
        val lat = recommendationPlacesList[position].coordinates?.latitude
        val lon = recommendationPlacesList[position].coordinates?.longitude
        val latLng: LatLng = LatLng(lat!!, lon!!)
        val location: CameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16f)
        map.animateCamera(location)

        addRecommendBtn.visibility = View.VISIBLE
        cancelRecommendBtn.visibility = View.VISIBLE

        var placeDetailsMap = hashMapOf<Any, Any>()
        val name: String? = recommendationPlacesList[position].name
        val placeId: String? = recommendationPlacesList[position].placeId
        val coordinates: GeoPoint? = recommendationPlacesList[position].coordinates
        val types: ArrayList<String>? = recommendationPlacesList[position].types
        val address: String? = recommendationPlacesList[position].address
        val openingHours: ArrayList<Any>? = recommendationPlacesList[position].openingHours
        val openingHoursText: ArrayList<String>? = recommendationPlacesList[position].openingHoursText
        val rating: Double? = recommendationPlacesList[position].rating
        val totalRatings: Int? = recommendationPlacesList[position].totalRatings

        placeDetailsMap["name"] = name.toString()
        placeDetailsMap["placeId"] = placeId.toString()
        placeDetailsMap["coordinates"] = coordinates as GeoPoint
        placeDetailsMap["types"] = types as ArrayList<String>
        placeDetailsMap["address"] = address.toString()
        placeDetailsMap["openingHours"] = openingHours as ArrayList<Any>
        placeDetailsMap["openingHoursText"] = openingHoursText as ArrayList<String>
        placeDetailsMap["rating"] = rating as Double
        placeDetailsMap["totalRatings"] = totalRatings as Int

        val placeDetail: PlaceDetails = PlaceDetails(null, name, placeId, coordinates, types, address, openingHours, openingHoursText, rating, totalRatings)

        var newPlaceMarker: Marker? = null
        newPlaceMarker = map.addMarker(
            MarkerOptions()
                .position(latLng)
                .title(placeId)
        )

        addRecommendBtn.setOnClickListener {
            for(place in placeDetailsArray){
                if(place.placeId == placeId){
                    Toast.makeText(context, "Place Already Added", Toast.LENGTH_SHORT).show()
                    addRecommendBtn.visibility = View.INVISIBLE
                    cancelRecommendBtn.visibility = View.INVISIBLE
                }else{
                    tripsReference.collection("places").add(placeDetailsMap)
                        .addOnSuccessListener {
                            Log.d(ContentValues.TAG, "Place Added Successfully")
                            Toast.makeText(context, "Place Added Successfully", Toast.LENGTH_SHORT).show()
                            placeDetailsArray.add(placeDetail)
                            addRecommendBtn.visibility = View.INVISIBLE
                            cancelRecommendBtn.visibility = View.INVISIBLE
                        }
                        .addOnFailureListener {
                            Log.d(ContentValues.TAG, "Could Not Add Place")

                        }
                }
            }

        }

        cancelRecommendBtn.setOnClickListener {
            newPlaceMarker?.remove()
            Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show()
            addRecommendBtn.visibility = View.INVISIBLE
            cancelRecommendBtn.visibility = View.INVISIBLE
        }

    }

    private fun isDocumentNull(docRef: DocumentSnapshot, field:String): Boolean{
        return docRef.data?.get(field) != null

    }//end isDocumentNull()


    private fun displayPlaceMarkers() {
        Log.d(ContentValues.TAG, "Map Fragment: ${placeDetailsArray}");

        for(place in placeDetailsArray){
            val placeLocation = LatLng(place.coordinates!!.latitude, place.coordinates!!.longitude)
            map.addMarker(
                MarkerOptions()
                    .position(placeLocation)
                    .title(place.placeId)
            )
        }
    }//end displayMarkers()


    private fun initPlacesAutocomplete() {
        Log.d(ContentValues.TAG, "initPlacesAutocomplete() called");

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

        var docId: String? = null
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

                val placeDetail = PlaceDetails(docId, placeName, placeId, placeCoordinates, placeTypesArray, placeAddress, placeOpeningHoursArray, placeOpeningHoursTextArray, placeRating, placeTotalRatings)
                Log.i(ContentValues.TAG, "Place Detail: $placeDetail")

                Log.i(ContentValues.TAG, "Place Detail Map: $placeDetailsMap")

                //set marker and animate camera
                val placeLocation = LatLng(placeCoordinates.latitude, placeCoordinates.longitude)
                var newPlaceMarker: Marker? = null
                newPlaceMarker = map.addMarker(
                    MarkerOptions()
                        .position(placeLocation)
                        .title(placeId)
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

                            //add place to locations
                            locationsReference.collection("places").add(placeDetailsMap)
                                .addOnSuccessListener {
                                    Log.d(ContentValues.TAG, "Place Added To Locations")
                                }//end addOnSuccessListener()
                                .addOnFailureListener{
                                    Log.d(ContentValues.TAG, "Place Failed to add to Locations")
                                }//end addOnFailureListener

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

        initRecommendationRecyclerView()

    }//end initAutocompletePlaces()



}//end class