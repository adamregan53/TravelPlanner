package com.example.travelplanner.activities

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.viewpager2.widget.ViewPager2
import com.example.travelplanner.data.PlaceDetails
import com.example.travelplanner.R
import com.example.travelplanner.adapters.ViewPagerAdapter
import com.example.travelplanner.databinding.ActivityPlacesBinding
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PlacesActivity : DrawerBaseActivity() {//end class

    private lateinit var binding: ActivityPlacesBinding

    //Firebase
    private lateinit var fStore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUserId: String
    lateinit var tripsReference: DocumentReference private set

    //Values From TripsActivity
    private lateinit var tripId: String
    var tripLatitude: Double = 0.0
    var tripLongitude: Double = 0.0

    //init place variables

    private lateinit var placeDetail: PlaceDetails
    lateinit var placeDetailsArray: ArrayList<PlaceDetails>

    lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlacesBinding.inflate(layoutInflater)
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

        placeDetailsArray = arrayListOf()

        retrievePlaces()

    }//end onCreate()


    private fun retrievePlaces() {
        tripsReference.collection("places").get()
            .addOnSuccessListener { result ->
                var placeName: String
                var placeId: String
                var placeCoordinates: GeoPoint
                var placeTypesArray: ArrayList<String>
                var placeAddress: String
                var placeOpeningHoursArray: ArrayList<Any>
                var placeOpeningHoursText: ArrayList<String>
                var placeRating: Double?
                var placeRatingTotal: Int?

                for (document in result) {
                    placeName = document.data["name"].toString()
                    placeId = document.id
                    placeCoordinates = document.data["coordinates"] as GeoPoint
                    placeTypesArray = ArrayList()
                    val arrayTypes = document.data["types"] as ArrayList<*>
                    for (type in arrayTypes) {
                        placeTypesArray.add(type.toString())
                    }
                    placeAddress = document.data["address"].toString()

                    placeOpeningHoursArray = ArrayList()
                    if(isDocumentNull(document, "openingHours")){
                        val placeOpeningHours = document.data["openingHours"] as ArrayList<Any>
                        for (openingHours in placeOpeningHours) {
                            val map = openingHours as HashMap<*, *>
                            placeOpeningHoursArray.add(map)
                        }
                    }

                    placeOpeningHoursText = ArrayList()
                    if(isDocumentNull(document, "openingHoursText")){
                        val openingHoursTextArray = document.data["openingHoursText"] as ArrayList<String>
                        for (openingHoursText in openingHoursTextArray){
                            placeOpeningHoursText.add(openingHoursText)
                        }
                    }

                    placeRating = null
                    if(isDocumentNull(document, "rating")){
                        placeRating = document.data["rating"] as Double
                    }

                    placeRatingTotal = null
                    if(isDocumentNull(document, "totalRatings")){
                        val ratingsTotalLong = document.data["totalRatings"] as Long
                        placeRatingTotal = ratingsTotalLong.toInt()
                    }

                    placeDetail = PlaceDetails(
                        placeName,
                        placeId,
                        placeCoordinates,
                        placeTypesArray,
                        placeAddress,
                        placeOpeningHoursArray,
                        placeOpeningHoursText,
                        placeRating,
                        placeRatingTotal
                    )

                    placeDetailsArray.add(placeDetail)

                }

                for(place in placeDetailsArray){
                    for (openingHours in place.openingHours!!){
                        val map = openingHours as HashMap<*, *>
                        Log.d(ContentValues.TAG, "PlacesActivity, openingHours: ${map}");
                    }


                    Log.d(ContentValues.TAG, "PlacesActivity, placeDetails: $place");

                }

                initFragments()
            }

    }//end retrievePlaces()

    private fun isDocumentNull(docRef:QueryDocumentSnapshot, field:String): Boolean{
        return docRef.data[field] != null

    }


    //initialises the tabs for places list and map
    private fun initFragments() {
        tabLayout = findViewById(R.id.tab_layout)
        viewPager2 = findViewById(R.id.view_pager2)
        viewPagerAdapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        viewPager2.adapter = viewPagerAdapter

        //disables swipe for changing tabs
        viewPager2.isUserInputEnabled = false

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    viewPager2.currentItem = tab.position

                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}


        })

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout.selectTab(tabLayout.getTabAt(position))
                Log.d(ContentValues.TAG, "PlacesActivity, OnPageChangeCallback: $position");
            }
        })

    }//end initFragments()


}