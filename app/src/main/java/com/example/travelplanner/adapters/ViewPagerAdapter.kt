package com.example.travelplanner.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import com.example.travelplanner.fragments.PlacesListFragment
import com.example.travelplanner.fragments.PlacesMapFragment
import androidx.viewpager2.adapter.FragmentStateAdapter as FragmentStateAdapter


class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle:Lifecycle): FragmentStateAdapter(fragmentManager, lifecycle)  {

    override fun getItemCount(): Int {
        return 2
    }//end getItemCount()

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> {
                PlacesListFragment()
            }
            1 -> {
                PlacesMapFragment()
            }
            else -> {
                PlacesListFragment()
            }
        }
    }//end createFragment()

}//end class