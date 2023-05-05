package com.example.travelplanner.data

import com.google.android.libraries.places.api.model.OpeningHours
import com.google.firebase.firestore.GeoPoint

data class PlaceDetails(
    val name: String?,
    val id: String?,
    val coordinates: GeoPoint,
    val types: ArrayList<String>,
    val address: String
)
