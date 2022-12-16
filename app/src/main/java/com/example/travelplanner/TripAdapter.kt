package com.example.travelplanner

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class TripAdapter(private val tripsList : ArrayList<TripDetails>) : RecyclerView.Adapter<TripAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.trip_cardview, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentTrip = tripsList[position]
        holder.tripTitle.text = currentTrip.trip_name
    }

    override fun getItemCount(): Int {
        return tripsList.size
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val tripImage: ImageView = itemView.findViewById(R.id.trip_image)
        val tripTitle: TextView = itemView.findViewById(R.id.trip_title)
        val tripDetail: TextView = itemView.findViewById(R.id.trip_detail)

    }


}