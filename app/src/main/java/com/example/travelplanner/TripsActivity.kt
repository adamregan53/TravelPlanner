package com.example.travelplanner

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.View.inflate
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.resources.Compatibility.Api21Impl.inflate
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travelplanner.databinding.ActivityLoginBinding.inflate
import com.google.android.material.navigation.NavigationBarItemView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text
import java.util.zip.Inflater

class TripsActivity : AppCompatActivity(){//end class

    private lateinit var fStore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUserId: String

    private lateinit var newRecyclerView: RecyclerView

    private lateinit var tripId: String
    private lateinit var tripName: String
    private lateinit var tripCoordinates: GeoPoint
    private lateinit var tripsList: ArrayList<TripDetails>

    private lateinit var newTripBtn: Button

    private lateinit var drawerLayout:DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trips)

        auth = Firebase.auth
        fStore = Firebase.firestore
        currentUserId = auth.currentUser?.uid.toString()

        newRecyclerView = findViewById(R.id.recyclerView)

        //tripsList = arrayListOf<TripDetails>()
        tripsList = Singleton.tripsList
        newTripBtn = this.findViewById(R.id.new_trip_btn)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.menu_Open, R.string.close_menu)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        navigationView.setNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.nav_home -> {
                    Log.d("MENU_DRAWER_TAG", "Home Item Selected");
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.nav_search -> {
                    Log.d("MENU_DRAWER_TAG", "Search Item Selected");
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.nav_account -> {
                    Log.d("MENU_DRAWER_TAG", "Account Item Selected");
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.nav_settings -> {
                    Log.d("MENU_DRAWER_TAG", "Settings Item Selected");
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.nav_logout -> {
                    Log.d("MENU_DRAWER_TAG", "Logout Item Selected");
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
            }
            true
        }

        retrieveTrips()

    }//end onCreate()

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun retrieveTrips() {
        if(Singleton.tripsList.isEmpty()){
            //reference to user document in database
            Log.d(TAG, "tripsList isEmpty: ${Singleton.tripsList.isEmpty()}");

            val userReference: DocumentReference = fStore.collection("users").document(currentUserId)
            userReference.collection("trips").get()
                .addOnSuccessListener { result ->
                    //get data from documents in trips collection
                    for(document in result){
                        Log.d(TAG, "${document.id} => ${document.data["trip_name"]}")
                        tripId = document.id
                        tripName = document.data["trip_name"].toString()
                        tripCoordinates = document.data["coordinates"] as GeoPoint
                        val tripDetail = TripDetails(tripId, tripName, tripCoordinates)
                        tripsList.add(tripDetail)
                    }

                    Log.w(TAG, "Retrieved trips from Firebase")

                    displayTrips()

                }
                .addOnFailureListener{
                    Log.w(TAG, "Error getting documents.")
                    Toast.makeText(applicationContext, "Failure", Toast.LENGTH_LONG).show()
                }
        }else{
            //Toast.makeText(this, "No trips available", Toast.LENGTH_SHORT).show()
            for(trip in tripsList){
                tripId = trip.id
                tripName = trip.name
                tripCoordinates = trip.coordinates
            }
            Log.w(TAG, "Retrieved trips from Singleton")

            displayTrips()
        }



    }//end retrieveTrips()



    private fun displayTrips() {

        newRecyclerView.layoutManager = LinearLayoutManager(this)

        //custom adapter for TripDetails data class
        var adapter = TripAdapter(tripsList)
        newRecyclerView.adapter = adapter
        adapter.setOnItemClickListener(object: TripAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                //Toast.makeText(this@TripsActivity, "You clicked on ${tripsList[position].trip_name}", Toast.LENGTH_SHORT).show()
                val tripId = tripsList[position].id
                val tripName = tripsList[position].name
                val coordinates = tripsList[position].coordinates
                Log.w(TAG, "tripId: $tripId, tripName: $tripName, coordinates: $coordinates")


                val intent =Intent(this@TripsActivity, PlaceActivity::class.java)
                intent.putExtra("tripId", tripId)
                /*
                intent.putExtra("tripName", tripName)
                intent.putExtra("latitude", coordinates.latitude)
                intent.putExtra("longitude", coordinates.longitude)
                */
                startActivity(intent)
            }

        })
    }//end displayTrips()

}