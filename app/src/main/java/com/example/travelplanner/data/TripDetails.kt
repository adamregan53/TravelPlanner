package com.example.travelplanner.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class TripDetails(
    var id: String,
    var name: String,
    var coordinates: GeoPoint,
    var startDate: Timestamp,
    var endDate: Timestamp
)
