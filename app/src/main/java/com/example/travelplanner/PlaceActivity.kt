package com.example.travelplanner

import android.annotation.SuppressLint
import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.example.travelplanner.databinding.ActivityPlaceBinding
import com.google.firebase.firestore.GeoPoint

class PlaceActivity : AppCompatActivity() {


    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place)

        val tripName: String = intent.getStringExtra("tripName") as String
        val tripLatitude: Double = intent.getDoubleExtra("latitude", 0.0)
        val tripLongitude: Double = intent.getDoubleExtra("longitude", 0.0)
        val tripCoordinates: GeoPoint = GeoPoint(tripLatitude, tripLongitude)

        //Toast.makeText(this@PlaceActivity, "Place view for $tripCoordinates", Toast.LENGTH_SHORT).show()

        val textString: TextView = findViewById(R.id.test_text)

        textString.text = tripName



    }
}
