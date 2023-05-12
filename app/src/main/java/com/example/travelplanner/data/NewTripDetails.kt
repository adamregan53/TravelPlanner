package com.example.travelplanner.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class NewTripDetails(
    var name: String,
    var coordinates: GeoPoint,
    var startDate: Timestamp,
    var endDate: Timestamp,
    var isItineraryGenerated: Boolean
)
