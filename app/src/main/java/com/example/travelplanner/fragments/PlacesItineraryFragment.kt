package com.example.travelplanner.fragments

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.travelplanner.R
import com.example.travelplanner.data.api.PostService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class PlacesItineraryFragment : Fragment() {

    private val service = PostService.create()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        GlobalScope.launch {
            val value = service.getPosts()

            Log.d(ContentValues.TAG, "PlacesItineraryFragment: $value");

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_places_itinerary, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}