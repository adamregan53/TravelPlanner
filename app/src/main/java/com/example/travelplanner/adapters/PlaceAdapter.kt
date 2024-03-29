package com.example.travelplanner.adapters;

import android.content.ContentValues
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.travelplanner.data.PlaceDetails
import com.example.travelplanner.R
import java.util.ArrayList;

class PlaceAdapter(private val placeDetailsList : ArrayList<PlaceDetails>) : RecyclerView.Adapter<PlaceAdapter.MyViewHolder>(){

    private lateinit var mListener: onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }//end onItemClickListener()

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener
    }//end setOnItemClickListener()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.place_cardview, parent, false)
        return MyViewHolder(v, mListener)
    }//end onCreateViewHolder()

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Log.d(ContentValues.TAG, "PlaceAdapter: position " + position);
        val currentPlace = placeDetailsList[position]
        holder.placeName.text = currentPlace.name
        holder.placeAddress.text = currentPlace.address
    }//end onBindViewHolder()

    override fun getItemCount(): Int {
        return placeDetailsList.size
    }//end getItemCount()

    class MyViewHolder(itemView: View, listener: onItemClickListener): RecyclerView.ViewHolder(itemView) {
        val placeName: TextView = itemView.findViewById(R.id.place_name)
        val placeAddress: TextView = itemView.findViewById(R.id.place_address)

        init {
            itemView.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }
        }
    }//end MyViewHolder()

}//end class
