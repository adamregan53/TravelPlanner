package com.example.travelplanner.adapters

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.travelplanner.R
import com.example.travelplanner.data.TripDetails
import java.text.SimpleDateFormat

class TripAdapter(private val tripsList : ArrayList<TripDetails>) : RecyclerView.Adapter<TripAdapter.MyViewHolder>() {

    private lateinit var mListener: onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }//end onItemClickListener()

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener
    }//end setOnItemClickListener()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.trip_cardview, parent, false)
        return MyViewHolder(v, mListener)
    }//end onCreateViewHolder()

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: position " + position);
        val currentTrip = tripsList[position]
        holder.tripTitle.text = currentTrip.name

        val currentStartDate = getDateString(currentTrip.startDate.seconds)
        holder.startDate.text = currentStartDate

        val currentEndDate = getDateString(currentTrip.endDate.seconds)
        holder.endDate.text = currentEndDate

    }//end onBindViewHolder()

    private val sdf = SimpleDateFormat("dd/MM/yyyy")
    private fun getDateString(time: Long) : String = sdf.format(time * 1000)

    override fun getItemCount(): Int {
        return tripsList.size
    }//end getItemCount()

    class MyViewHolder(itemView: View, listener: onItemClickListener): RecyclerView.ViewHolder(itemView){

        val tripTitle: TextView = itemView.findViewById(R.id.trip_title)
        val startDate: TextView = itemView.findViewById(R.id.trip_start_date)
        val endDate: TextView = itemView.findViewById(R.id.trip_end_date)

        init {
            itemView.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }
        }

    }//end MyViewHolder()


}//end class