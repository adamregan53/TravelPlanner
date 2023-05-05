package com.example.travelplanner.fragments

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travelplanner.R
import com.example.travelplanner.activities.PlacesActivity
import com.example.travelplanner.activities.TripsActivity
import com.example.travelplanner.adapters.TripAdapter
import com.example.travelplanner.data.TripDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class TripsListFragment : Fragment() {

    //Firestore
    private lateinit var fStore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var tripsActivity: TripsActivity

    //recycler view
    private lateinit var tripsRecyclerView: RecyclerView
    private lateinit var adapter: TripAdapter

    //trips list
    private lateinit var tripsList: ArrayList<TripDetails>


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        auth = Firebase.auth
        fStore = Firebase.firestore

        tripsActivity = activity as TripsActivity

        tripsList = tripsActivity.tripsList

        tripsRecyclerView = view?.findViewById(R.id.tripsRecyclerView)!!

        displayTrips()

    }//end onActivityCreated()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trips_list, container, false)
    }//end onCreateView()

    private fun displayTrips() {
        tripsRecyclerView.layoutManager = LinearLayoutManager(this.tripsActivity)
        //custom adapter for TripDetails data class
        var adapter = TripAdapter(tripsList)
        tripsRecyclerView.adapter = adapter
        adapter.setOnItemClickListener(object: TripAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                //Toast.makeText(this@TripsActivity, "You clicked on ${tripsList[position].trip_name}", Toast.LENGTH_SHORT).show()
                val tripId = tripsList[position].id
                val tripName = tripsList[position].name
                val coordinates = tripsList[position].coordinates
                Log.w(TAG, "tripId: $tripId, tripName: $tripName, coordinates: $coordinates")

                val intent = Intent(tripsActivity, PlacesActivity::class.java)
                intent.putExtra("tripId", tripId)//used for Firebase Document Reference
                intent.putExtra("tripLatitude", coordinates.latitude)
                intent.putExtra("tripLongitude", coordinates.longitude)
                startActivity(intent)
            }

        })

        val dividerItemDecoration: DividerItemDecoration = DividerItemDecoration(this.tripsActivity, DividerItemDecoration.VERTICAL)
        tripsRecyclerView.addItemDecoration(dividerItemDecoration)
    }//end displayTrips()


}//end class