package com.example.travelplanner

import com.google.firebase.firestore.GeoPoint

data class TripDetails(
    var id: String,
    var name: String,
    var coordinates: GeoPoint
)
