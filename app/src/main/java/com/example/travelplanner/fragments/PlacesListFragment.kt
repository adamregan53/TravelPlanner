package com.example.travelplanner.fragments

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.travelplanner.data.PlaceDetails
import com.example.travelplanner.R
import com.example.travelplanner.activities.PlacesActivity
import com.example.travelplanner.adapters.PlaceAdapter
import com.example.travelplanner.data.SharedData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList


class PlacesListFragment() : Fragment() {

    //Firebase
    private lateinit var fStore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var tripsReference: DocumentReference

    private lateinit var placesActivity: PlacesActivity
    private lateinit var placeMapFragment: PlacesMapFragment

    //recycler view
    private lateinit var placesRecyclerView: RecyclerView
    private lateinit var adapter: PlaceAdapter

    //place details
    private lateinit var placeDetailsArray: ArrayList<PlaceDetails>


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        auth = Firebase.auth
        fStore = Firebase.firestore

        placesActivity = activity as PlacesActivity
        placeMapFragment = PlacesMapFragment()
        tripsReference = placesActivity.tripsReference

        placeDetailsArray = placesActivity.placeDetailsArray

        placesRecyclerView = view?.findViewById(R.id.placesRecyclerView)!!


        Log.d(ContentValues.TAG, "Place List Fragment: ${placeDetailsArray}");

        displayPlaces()
    }//end onActivityCreated()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_places_list, container, false)

    }//end OnCreateView()


    private fun displayPlaces() {
        placesRecyclerView.layoutManager = LinearLayoutManager(this.placesActivity)

        adapter = PlaceAdapter(placeDetailsArray)
        placesRecyclerView.adapter = adapter
        adapter.setOnItemClickListener(object: PlaceAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                //switch tab to map view
                val tabIndex = placesActivity.tabLayout.getTabAt(1)
                placesActivity.tabLayout.selectTab(tabIndex)

            }

        })

        val dividerItemDecoration = DividerItemDecoration(this.placesActivity, DividerItemDecoration.VERTICAL)
        placesRecyclerView.addItemDecoration(dividerItemDecoration)

        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(placesRecyclerView)

    }//end displayPlaces()


    //change order of recycler view
    private val simpleCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN or
                    ItemTouchHelper.START or ItemTouchHelper.END, ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: ViewHolder,
                target: ViewHolder
            ): Boolean {
                val fromPos = viewHolder.adapterPosition
                val toPos = target.adapterPosition
                Collections.swap(placeDetailsArray, fromPos, toPos)
                // move item in `fromPos` to `toPos` in adapter.

                adapter.notifyItemMoved(fromPos, toPos)

                return false // true if moved, false otherwise
            }//end onMove()

            override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
                // remove from adapter
                val position = viewHolder.adapterPosition

                when(direction){
                    ItemTouchHelper.LEFT -> {
                        placeDetailsArray.removeAt(position)
                        adapter.notifyItemRemoved(position)
                    }
                }
            }//end onSwiped()

        }//end simpleCallback


}//end class