package com.example.travelplanner.data.api

@kotlinx.serialization.Serializable
data class PostRequest (
    val docId: String,
    val placeId: String,
    val locationRef: String,
    val name: String,
    val types: ArrayList<String>
)
