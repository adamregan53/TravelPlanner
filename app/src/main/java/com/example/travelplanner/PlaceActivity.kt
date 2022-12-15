package com.example.travelplanner

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView

class PlaceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place)

        val arrayList: ArrayList<String> = intent.getSerializableExtra("trips") as ArrayList<String>
        val listPosition: Int = intent.extras?.get("position") as Int

        val textString: TextView = findViewById<TextView>(R.id.test_text)


            Log.d(ContentValues.TAG, arrayList[listPosition])


    }
}