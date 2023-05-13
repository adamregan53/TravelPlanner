package com.example.travelplanner.data.api

@kotlinx.serialization.Serializable
data class PostResponse (
    val docId: String,
    val name: String,
    val placeId: String
)