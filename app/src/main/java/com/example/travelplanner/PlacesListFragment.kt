package com.example.travelplanner

import android.content.ClipData.Item
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.libraries.places.api.model.Place
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList


class PlacesListFragment : Fragment() {

    private lateinit var tripId: String

    //Firestore
    private lateinit var fStore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUserId: String
    private lateinit var tripsReference: DocumentReference

    private lateinit var testPlacesActivity: TestPlacesActivity

    //recycler view
    private lateinit var placeRecyclerView: RecyclerView
    private lateinit var adapter:PlaceAdapter

    //place details
    private lateinit var placeName: String
    private lateinit var placeId: String
    private lateinit var placeCoordinates: GeoPoint
    private lateinit var placeTypesArray: ArrayList<String>
    private lateinit var placeAddress: String
    private lateinit var placeDetail: PlaceDetails
    private lateinit var placeDetailsArray: ArrayList<PlaceDetails>


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        auth = Firebase.auth
        fStore = Firebase.firestore

        testPlacesActivity = activity as TestPlacesActivity
        tripsReference = testPlacesActivity.tripsReference

        placeDetailsArray = testPlacesActivity.placeDetailsArray

        placeRecyclerView = view?.findViewById<RecyclerView>(R.id.testRecyclerView)!!

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
        placeRecyclerView.layoutManager = LinearLayoutManager(this.testPlacesActivity)

        adapter = PlaceAdapter(placeDetailsArray)
        placeRecyclerView.adapter = adapter
        adapter.setOnItemClickListener(object: PlaceAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {

            }

        })

        val dividerItemDecoration:DividerItemDecoration = DividerItemDecoration(this.testPlacesActivity, DividerItemDecoration.VERTICAL)
        placeRecyclerView.addItemDecoration(dividerItemDecoration)

        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(placeRecyclerView)

    }//end displayPlaces()


    //change order of recycler view
    val simpleCallback = object : ItemTouchHelper.SimpleCallback(
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
            }

            override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
                // remove from adapter
                val position = viewHolder.adapterPosition

                when(direction){
                    ItemTouchHelper.LEFT -> {
                        placeDetailsArray.removeAt(position)
                        adapter.notifyItemRemoved(position)
                    }
                }
            }
        }


}//end class