package com.example.travelplanner.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class TripDetails(
    var id: String,
    var address: String?,
    var coordinates: GeoPoint,
    var endDate: Timestamp,
    @field:JvmField
    var isItineraryGenerated: Boolean,
    var locationId: String,
    var locationRef: String,
    var name: String,
    var startDate: Timestamp,
    var types: ArrayList<String>?


)
