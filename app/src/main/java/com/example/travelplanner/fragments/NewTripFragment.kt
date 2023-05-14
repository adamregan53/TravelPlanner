package com.example.travelplanner.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travelplanner.R
import com.example.travelplanner.activities.PlacesActivity
import com.example.travelplanner.activities.TripsActivity
import com.example.travelplanner.adapters.TripSuggestionAdapter
import com.example.travelplanner.data.NewTripDetails
import com.example.travelplanner.data.SharedData
import com.example.travelplanner.data.TripDetails
import com.example.travelplanner.data.TripSuggestion
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat.CLOCK_24H
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.datatype.DatatypeConstants.MONTHS
import kotlin.collections.ArrayList


class NewTripFragment : Fragment(){//end class
    //firebase
    private lateinit var fStore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUserId: String

    private lateinit var tripsActivity: TripsActivity
    private lateinit var tripsListFragment: TripsListFragment
    private lateinit var cancelTripBtn: Button

    //date selection UI
    private lateinit var enterTripCardView: CardView
    private lateinit var tripDisplayTitle: TextView
    private lateinit var startDateTxt: TextView
    private lateinit var endDateTxt: TextView
    private lateinit var setDateBtn: Button
    private lateinit var confirmTripInfoBtn: Button
    private lateinit var cancelTripInfoBtn: Button


    private var tripStartDate: Date? = null
    private var tripEndDate: Date? = null


    //new trip values
    private lateinit var newTripAddress: String
    private lateinit var newTripCoordinates: GeoPoint
    private lateinit var newEndDate: Timestamp
    var isNewItineraryGenerated: Boolean = false
    private lateinit var newLocationId: String
    private lateinit var newLocationRef: String
    private lateinit var newTripName: String
    private lateinit var newStartDate: Timestamp
    private lateinit var newTripTypes: ArrayList<String>

    //recycler view
    private lateinit var tripSuggestionRecyclerView: RecyclerView
    private lateinit var adapterSuggestion: TripSuggestionAdapter

    //trips suggestion list
    private lateinit var tripSuggestionList: ArrayList<TripSuggestion>

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(ContentValues.TAG, "NewTripMapFragment Started");

        auth = Firebase.auth
        fStore = Firebase.firestore
        currentUserId = auth.currentUser?.uid.toString()

        tripsActivity = activity as TripsActivity
        cancelTripBtn = view?.findViewById(R.id.cancel_trip_btn)!!

        tripSuggestionList = arrayListOf()

        retrieveTripSuggestions()

    }//end onActivityCreated()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_trip, container, false)
    }//end onCreateView()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tripSuggestionRecyclerView = view.findViewById(R.id.trip_suggestion_recyclerview)

        enterTripCardView = view.findViewById(R.id.enter_trip_info)
        tripDisplayTitle = view.findViewById(R.id.trip_display_title)
        startDateTxt = view.findViewById(R.id.start_date_txt)
        endDateTxt = view.findViewById(R.id.end_date_txt)
        setDateBtn = view.findViewById(R.id.set_date_btn)
        confirmTripInfoBtn = view.findViewById(R.id.confirm_trip_info_btn)
        cancelTripInfoBtn = view.findViewById(R.id.cancel_trip_info_btn)

        startDateTxt.text = ""
        endDateTxt.text = ""

        setDateBtn.setOnClickListener{
            showDateRangePicker()
        }

        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm")
        fun getDateString(time: Long) : String = sdf.format(time * 1000)

        confirmTripInfoBtn.setOnClickListener{
            if(tripStartDate != null && tripEndDate != null){
                newStartDate = Timestamp(tripStartDate!!)
                newEndDate = Timestamp(tripEndDate!!)
                Log.w(ContentValues.TAG, "Confirm Trip: newStartDate TimeStamp -> ${getDateString(newStartDate.seconds)}")
                Log.w(ContentValues.TAG, "Confirm Trip: newEndDate TimeStamp -> ${getDateString(newEndDate.seconds)}")


                addNewTrip()
            }
            else if(tripStartDate == null && tripEndDate != null){
                Toast.makeText(
                    this.tripsActivity,
                    "start date is empty",
                    Toast.LENGTH_SHORT
                ).show()
            }else if(tripEndDate == null && tripStartDate != null){
                Toast.makeText(
                    this.tripsActivity,
                    "end date is empty",
                    Toast.LENGTH_SHORT
                ).show()
            }else{
                Toast.makeText(
                    this.tripsActivity,
                    "enter start and end dates",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }//end confirmTripInfoBtn()

        cancelTripInfoBtn.setOnClickListener{
            tripStartDate = null
            tripEndDate = null
            startDateTxt.text = ""
            endDateTxt.text = ""
            tripDisplayTitle.text = ""
            enterTripCardView.visibility = View.GONE
        }

    }//end onViewCreated()




    private fun showDateRangePicker(){
        val dateRangePicker = MaterialDatePicker.Builder
            .dateRangePicker()
            .setTitleText("Select Dates For Trip")
            .build()

        dateRangePicker.show(childFragmentManager, "dateRangePicker")

        dateRangePicker.addOnPositiveButtonClickListener { datePicked ->
            val startDateLong = datePicked.first
            val endDateLong = datePicked.second
            val startDate: Date = Date(startDateLong)
            val endDate: Date = Date(endDateLong)

            setArrivalTime(startDate, endDate)
        }
        dateRangePicker.addOnNegativeButtonClickListener {
            Log.w(ContentValues.TAG, "DatePicker: Negative")

        }

    }

    private fun setArrivalTime(startDate: Date, endDate: Date) {
        val timePicker1 = MaterialTimePicker.Builder()
            .setTimeFormat(CLOCK_24H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Select Arrival Time")
            .build()
        timePicker1.show(childFragmentManager, "TAG")

        timePicker1.addOnPositiveButtonClickListener{ timePicked ->
            val hour = timePicker1.hour
            val minute = timePicker1.minute

            tripStartDate = addHoursToDate(startDate, hour, minute)
            Log.w(ContentValues.TAG, "TimePicker1: Positive -> ${tripStartDate}")


            setDepartureTime(endDate)
        }
        timePicker1.addOnNegativeButtonClickListener {
            Log.w(ContentValues.TAG, "TimePicker1: Negative")

        }
    }

    private fun setDepartureTime(endDate: Date) {
        val timePicker2 = MaterialTimePicker.Builder()
            .setTimeFormat(CLOCK_24H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Select Departure Time")
            .build()
        timePicker2.show(childFragmentManager, "TAG")

        timePicker2.addOnPositiveButtonClickListener { timePicked ->
            val hour = timePicker2.hour
            val minute = timePicker2.minute

            tripEndDate = addHoursToDate(endDate, hour, minute)
            Log.w(ContentValues.TAG, "TimePicker2: Positive -> ${tripEndDate}")


            displayTripDates()
        }
        timePicker2.addOnNegativeButtonClickListener {
            Log.w(ContentValues.TAG, "TimePicker2: Negative")
        }
    }

    private fun displayTripDates() {
        if(tripStartDate != null && tripEndDate != null){
            startDateTxt.text = convertDateToString(tripStartDate!!)
            endDateTxt.text = convertDateToString(tripEndDate!!)
        }

    }

    private fun addHoursToDate(date: Date, hours: Int, minutes: Int): Date {
        val calendar: Calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.HOUR_OF_DAY, hours - 1)
        calendar.add(Calendar.MINUTE, minutes)
        return calendar.time
    }

    private fun convertDateToTimestamp() {

    }

    private fun convertLongToDate(time:Long):String{
        val date = Date(time)
        val format = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())

        return format.format(date)
    }

    private fun convertDateToString(date:Date):String{
        val format = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())

        return format.format(date)
    }


    private fun retrieveTripSuggestions() {
        Log.w(ContentValues.TAG, "retrieveTripSuggestions() called")
        var locationDocId: String
        var locationId: String
        var locationName: String
        var locationAddress: String
        var locationCoordinates: GeoPoint
        var locationTypes: ArrayList<String> = ArrayList()
        var tripSuggestion: TripSuggestion

        if(SharedData.tripSuggestionList.isEmpty()) {
            fStore.collection("locations").get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        locationDocId = document.id
                        locationAddress = document.data["address"].toString()
                        locationCoordinates = document.data["coordinates"] as GeoPoint
                        locationId = document.data["id"].toString()
                        locationName = document.data["name"].toString()
                        val types = document.data["types"] as ArrayList<String>
                        for (type in types) {
                            locationTypes.add(type.toString())
                        }

                        tripSuggestion = TripSuggestion(
                            locationDocId,
                            locationAddress,
                            locationCoordinates,
                            locationId,
                            locationName,
                            types
                        )
                        Log.w(ContentValues.TAG, "tripsSuggestion: $tripSuggestion")
                        tripSuggestionList.add(tripSuggestion)
                        SharedData.tripSuggestionList.add(tripSuggestion)
                    }
                    displayTripSuggestions()

                    Log.w(ContentValues.TAG, "tripsSuggestionList: ${tripSuggestionList[0]}")
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this.tripsActivity,
                        "Could not retrieve data from Firebase",
                        Toast.LENGTH_SHORT
                    ).show()

                }
        }else{
            tripSuggestionList = SharedData.tripSuggestionList
            Log.w(ContentValues.TAG, "Retrieved from SharedData")

            displayTripSuggestions()
        }

    }//end retrieveTrips()


    private fun displayTripSuggestions() {
        Log.w(ContentValues.TAG, "displayTripsSuggestions() called")

        tripSuggestionRecyclerView.layoutManager = LinearLayoutManager(this.tripsActivity)
        adapterSuggestion = TripSuggestionAdapter(tripSuggestionList)
        tripSuggestionRecyclerView.adapter = adapterSuggestion
        adapterSuggestion.setOnItemClickListener(object: TripSuggestionAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                Log.w(ContentValues.TAG, "Trip Suggestion: ${tripSuggestionList[position]}")
                enterTripCardView.visibility = View.VISIBLE
                tripDisplayTitle.text = tripSuggestionList[position].name

                newTripAddress = tripSuggestionList[position].address
                newTripName = tripSuggestionList[position].name
                newTripCoordinates = tripSuggestionList[position].locationCoordinates
                isNewItineraryGenerated = false
                newLocationId = tripSuggestionList[position].locationId
                newLocationRef = tripSuggestionList[position].docId
                val types = tripSuggestionList[position].types
                newTripTypes = ArrayList()
                for(type in types){
                    newTripTypes.add(type)
                }
            }

        })
        initFragments()

    }//end displayTripSuggestions()

    private fun addNewTrip() {
        var newTripMap= hashMapOf<Any, Any>()
        newTripMap["address"] = newTripAddress
        newTripMap["coordinates"] = newTripCoordinates
        newTripMap["endDate"] = newEndDate
        newTripMap["isItineraryGenerated"] = isNewItineraryGenerated
        newTripMap["locationId"] = newLocationId
        newTripMap["locationRef"] = newLocationRef
        newTripMap["name"] = newTripName
        newTripMap["startDate"] = newStartDate
        newTripMap["types"] = newTripTypes

        fStore.collection("users").document(currentUserId).collection("trips").add(newTripMap)
            .addOnSuccessListener {

                Log.d(ContentValues.TAG, "Attempting to load NewTripMapFragment");
                val tripListFragment: TripsListFragment = TripsListFragment()
                tripsActivity.supportFragmentManager.beginTransaction().apply {
                    replace(R.id.tripsFlFragment, tripListFragment)
                    commit()
                }
                Toast.makeText(
                    this.tripsActivity,
                    "Successfully added new trip",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener{
                Toast.makeText(
                    this.tripsActivity,
                    "Failed to add new trip",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }//end addNewTrip()


    private fun initFragments() {
        Log.w(ContentValues.TAG, "initFragments() called")
        tripsListFragment = TripsListFragment()

        cancelTripBtn.setOnClickListener{
            Log.d(ContentValues.TAG, "Attempting to load TripsListFragment");
            tripsActivity.supportFragmentManager.beginTransaction().apply {
                replace(R.id.tripsFlFragment, tripsListFragment)
                commit()
            }
        }

    }//end initFragments




}