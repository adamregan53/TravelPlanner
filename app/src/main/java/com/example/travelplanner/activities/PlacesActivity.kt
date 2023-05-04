package com.example.travelplanner.activities

import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.example.travelplanner.data.PlaceDetails
import com.example.travelplanner.R
import com.example.travelplanner.adapters.ViewPagerAdapter
import com.example.travelplanner.databinding.ActivityPlacesBinding
import com.google.android.libraries.places.api.model.OpeningHours
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PlacesActivity : DrawerBaseActivity() {

    private lateinit var binding: ActivityPlacesBinding

    //Firebase
    private lateinit var fStore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUserId: String
    lateinit var tripsReference: DocumentReference

    //Values From TripsActivity
    private lateinit var tripId: String
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

    private lateinit var tabLayout: TabLayout
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

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })

    }//end initFragments()


}//end class