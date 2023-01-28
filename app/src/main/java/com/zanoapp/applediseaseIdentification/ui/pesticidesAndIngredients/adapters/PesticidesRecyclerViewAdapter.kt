package com.zanoapp.applediseaseIdentification.ui.pesticidesAndIngredients.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zanoapp.applediseaseIdentification.databinding.CardPesticidesBinding
import com.zanoapp.applediseaseIdentification.localDataPersistence.pesticidesDB.Pesticide

class PesticidesRecyclerViewAdapter :
    ListAdapter<Pesticide, PesticidesRecyclerViewAdapter.ViewHolder>(
        PesticidesDiffUtilCallBack()
    ) {

    lateinit var binding: CardPesticidesBinding

    inner class ViewHolder() :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Pesticide) {

            binding.apply {
                pesticideNameTextView.text = item.pesticideName
                activeMaterialTextView.text = item.pesticideIngredients
                typeOfPreparateTextView.text = item.pesticideType
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding =
            CardPesticidesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = currentList[position]
        holder.bind(item)
    }

    class PesticidesDiffUtilCallBack : DiffUtil.ItemCallback<Pesticide>() {
        override fun areItemsTheSame(oldItem: Pesticide, newItem: Pesticide): Boolean {
            return oldItem.pesticideId == newItem.pesticideId
        }

        override fun areContentsTheSame(oldItem: Pesticide, newItem: Pesticide): Boolean {
            return oldItem == newItem
        }
    }
}