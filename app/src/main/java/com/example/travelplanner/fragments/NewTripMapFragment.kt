package com.example.travelplanner.fragments

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.travelplanner.R
import com.example.travelplanner.activities.TripsActivity


class NewTripMapFragment : Fragment() {

    private lateinit var tripsActivity: TripsActivity
    private lateinit var tripsListFragment: TripsListFragment
    private lateinit var cancelTripBtn: Button

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(ContentValues.TAG, "NewTripMapFragment Started");

        tripsActivity = activity as TripsActivity
        cancelTripBtn = view?.findViewById(R.id.cancel_trip_btn)!!

        initFragments()

    }//end onActivityCreated()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_trip_map, container, false)
    }//end onCreateView()

    private fun initFragments() {
        tripsListFragment = TripsListFragment()

        cancelTripBtn.setOnClickListener{
            Log.d(ContentValues.TAG, "Attempting to load TripsListFragment");
            tripsActivity.supportFragmentManager.beginTransaction().apply {
                replace(R.id.tripsFlFragment, tripsListFragment)
                commit()
            }
        }

    }//end initFragments

}//end class