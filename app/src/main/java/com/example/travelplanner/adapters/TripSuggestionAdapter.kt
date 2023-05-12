package com.example.travelplanner.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.travelplanner.R
import com.example.travelplanner.data.TripSuggestion

class TripSuggestionAdapter(private val tripsSuggestionList : ArrayList<TripSuggestion>) : RecyclerView.Adapter<TripSuggestionAdapter.MyViewHolder>() {
    private lateinit var mListener: onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }//end onItemClickListener()

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.trip_suggestion_cardview, parent, false)
        return MyViewHolder(v, mListener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentTripSuggestion = tripsSuggestionList[position]

        holder.tripSuggestionTitle.text = currentTripSuggestion.name
        holder.tripSuggestionAddress.text = currentTripSuggestion.address
    }

    override fun getItemCount(): Int {
        return tripsSuggestionList.size
    }

    class MyViewHolder(itemView: View, listener: onItemClickListener): RecyclerView.ViewHolder(itemView){
        val tripSuggestionTitle: TextView = itemView.findViewById(R.id.trip_suggestion_title)
        val tripSuggestionAddress: TextView = itemView.findViewById(R.id.trip_suggestion_address)

        init {
            itemView.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }
        }
    }
}