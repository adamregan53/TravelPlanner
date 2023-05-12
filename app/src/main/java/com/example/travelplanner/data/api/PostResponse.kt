package com.example.travelplanner.data.api

@kotlinx.serialization.Serializable
data class PostResponse (
    val body: String,
    val title: String,
    val id: Int,
    val userId: Int
)