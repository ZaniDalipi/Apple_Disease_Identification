package com.zanoapp.applediseaseIdentification.uiController.locations

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider

import com.zanoapp.applediseaseIdentification.R

class SavedLocationsFragment : Fragment() {

    companion object {
        fun newInstance() = SavedLocationsFragment()
    }

    private lateinit var viewModel: SavedLocationsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.saved_locations_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SavedLocationsViewModel::class.java)

    }

}
