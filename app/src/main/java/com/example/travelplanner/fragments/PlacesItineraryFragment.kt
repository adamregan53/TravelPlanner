package com.example.travelplanner.fragments

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.travelplanner.R
import com.example.travelplanner.data.api.PostRequest
import com.example.travelplanner.data.api.PostResponse
import com.example.travelplanner.data.api.PostService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class PlacesItineraryFragment : Fragment() {

    private val service = PostService.create()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val docId = "docIdTest"
        val placeId = "placeIdTest"
        val locationRef = "k7YH8X1OAyCJOg6bs7KS"
        val name = "nameTest"
        val types: ArrayList<String> = ArrayList()
        types.add("BAR")
        types.add("POINT_OF_INTEREST")
        types.add("ESTABLISHMENT")

        val post = PostRequest(docId, placeId, locationRef, name, types)
        Log.d(ContentValues.TAG, "Post Request: ${post.placeId}");

        GlobalScope.launch {

            val postResponse = service.createPost(post)
            Log.d(ContentValues.TAG, "Post Response: $postResponse");

            if (postResponse != null) {
                for (post in postResponse){
                    Log.d(ContentValues.TAG, "Post Response: docId: ${post.docId}, placeId: ${post.placeId}, name: ${post.name}");
                }
            }

        }//end GlobalScope

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