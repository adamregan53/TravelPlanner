package com.example.travelplanner

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class PlacesListFragment : Fragment() {

    private lateinit var tripId: String

    //Firestore
    private lateinit var fStore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUserId: String
    private lateinit var tripsReference: DocumentReference

    private lateinit var testPlacesActivity: TestPlacesActivity

    //recycler view
    private lateinit var placeRecyclerView: RecyclerView

    //place details
    private lateinit var placeName: String
    private lateinit var placeId: String
    private lateinit var placeCoordinates: GeoPoint
    private lateinit var placeTypesArray: ArrayList<String>
    private lateinit var placeAddress: String
    private lateinit var placeDetail: PlaceDetails
    private lateinit var placeDetailsArray: ArrayList<PlaceDetails>


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        auth = Firebase.auth
        fStore = Firebase.firestore

        testPlacesActivity = activity as TestPlacesActivity
        tripsReference = testPlacesActivity.tripsReference

        placeDetailsArray = testPlacesActivity.placeDetailsArray

        placeRecyclerView = view?.findViewById<RecyclerView>(R.id.testRecyclerView)!!

        Log.d(ContentValues.TAG, "Place List Fragment: ${placeDetailsArray}");



        displayPlaces()
    }//end onActivityCreated()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_places_list, container, false)

    }//end OnCreateView()

    private fun displayPlaces() {
        placeRecyclerView.layoutManager = LinearLayoutManager(this.testPlacesActivity)

        var adapter = PlaceAdapter(placeDetailsArray)
        placeRecyclerView.adapter = adapter
        adapter.setOnItemClickListener(object: PlaceAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {

            }

        })
    }//end displayPlaces()


}//end class