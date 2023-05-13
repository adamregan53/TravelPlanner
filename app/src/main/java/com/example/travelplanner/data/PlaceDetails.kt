package com.example.travelplanner.data

import com.google.firebase.firestore.GeoPoint

data class PlaceDetails(
    val docId: String?,
    val name: String?,
    val placeId: String?,
    val coordinates: GeoPoint?,
    val types: ArrayList<String>?,
    val address: String?,
    val openingHours: ArrayList<Any>?,
    val openingHoursText: ArrayList<String>?,
    val rating: Double?,
    val totalRatings: Int?
)
