package com.example.travelplanner.data

import com.google.firebase.firestore.GeoPoint

data class TripSuggestion(
    var docId: String,
    var address: String,
    var locationCoordinates: GeoPoint,
    var locationId: String,
    var name: String,
    var types: ArrayList<String>,
    var utcOffset: Int
)
