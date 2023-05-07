package com.example.travelplanner.fragments

import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import com.example.travelplanner.R
import com.google.android.material.button.MaterialButton


class TestExpandingList : Fragment() {
    private lateinit var placeCardView: CardView
    private lateinit var expandPlaceCardBtn: Button
    private lateinit var expandableDetailsLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_test_expanding_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        placeCardView = view.findViewById(R.id.place_card_test)
        expandPlaceCardBtn = view.findViewById(R.id.expand_place_card_btn)
        expandableDetailsLayout = view.findViewById(R.id.expandable_details_layout)
        expandPlaceCardBtn.setOnClickListener{
            if(expandableDetailsLayout.visibility == View.GONE){
                TransitionManager.beginDelayedTransition(placeCardView, AutoTransition())
                expandableDetailsLayout.visibility = View.VISIBLE
            }else{
                TransitionManager.beginDelayedTransition(placeCardView, AutoTransition())
                expandableDetailsLayout.visibility = View.GONE
            }
        }

    }

}