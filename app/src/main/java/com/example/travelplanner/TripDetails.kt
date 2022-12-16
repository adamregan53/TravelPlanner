package com.example.travelplanner

import com.google.firebase.firestore.GeoPoint

data class TripDetails(var trip_name: String, var coordinates: GeoPoint)
