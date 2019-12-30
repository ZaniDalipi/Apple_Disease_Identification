package com.zanoapp.applediseaseindentificator.uiController.locations

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zanoapp.applediseaseindentificator.databinding.SavedLocationsFragmentBinding
import com.zanoapp.applediseaseindentificator.models.SavedLocation

class LocationsAdapter: ListAdapter<SavedLocation, LocationsAdapter.ViewHolder>(LocationAdapterDiffCallback()) {

    class LocationAdapterDiffCallback:  DiffUtil.ItemCallback<SavedLocation>(){
        override fun areItemsTheSame(oldItem: SavedLocation, newItem: SavedLocation): Boolean {
            return TODO("here are are cheching if the items are the same if there is not a new item in it added")
        }

        override fun areContentsTheSame(oldItem: SavedLocation, newItem: SavedLocation): Boolean {
            return TODO("here are are cheching if the items content are the same if there is not a new item in it added")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class ViewHolder private constructor(itemView: View) :RecyclerView.ViewHolder(itemView){

        fun bind(item: SavedLocation){

        }

        companion object{
            fun from(parent: ViewGroup): ViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = SavedLocationsFragmentBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding.root)
            }
        }
    }
}
