package com.example.travelplanner.data

import com.google.android.gms.maps.GoogleMap

object SharedData {
    var tripsList: ArrayList<TripDetails> = arrayListOf()
    var tripSuggestionList: ArrayList<TripSuggestion> = arrayListOf()
    var placesList: ArrayList<ArrayList<PlaceDetails>> = arrayListOf()
}