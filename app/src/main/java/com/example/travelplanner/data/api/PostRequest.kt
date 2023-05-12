package com.example.travelplanner.data.api

@kotlinx.serialization.Serializable
data class PostRequest (
    val body: String,
    val title: String,
    val userId: Int
)
