package com.example.travelplanner

import com.google.firebase.firestore.GeoPoint

data class PlaceDetails(
    val name: String?,
    val id: String?,
    val coordinates: GeoPoint,
    val types: ArrayList<String>,
    val address: String
)
