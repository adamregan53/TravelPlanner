package com.example.travelplanner.data

import com.google.firebase.firestore.GeoPoint

data class PlaceDetails(
    val name: String?,
    val id: String?,
    val coordinates: GeoPoint,
    val types: ArrayList<String>?,
    val address: String?,
    val openingHours: ArrayList<Any>?,
    val openingHoursText: ArrayList<String>?,
    val rating: Double?,
    val totalRatings: Int?
)
