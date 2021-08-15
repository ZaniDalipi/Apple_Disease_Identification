package com.zanoapp.applediseaseIdentification.ui.managementAndAnalytics.adapters

import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zanoapp.applediseaseIdentification.R
import com.zanoapp.applediseaseIdentification.databinding.CardTransactionsBinding
import com.zanoapp.applediseaseIdentification.localDataPersistence.transactionsDB.Transaction
import com.zanoapp.applediseaseIdentification.utils.setTransactionImage
import java.security.AccessController.getContext
import kotlin.math.roundToInt


class TransactionRecyclerViewAdapterRecyclerView :
    ListAdapter<Transaction, TransactionRecyclerViewAdapterRecyclerView.ViewHolder>(
        TransactionDiffUtilsCallback()
    ) {

    lateinit var binding: CardTransactionsBinding
    /*try creating an array and populate it from the viewholder*/


    inner class ViewHolder() :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Transaction) {
            val transactionTotal = item.calculateTotal()
            binding.productNameTextView.text = item.productName
            binding.transactionDate.text = item.saleDate
            binding.transactionAmount.text = transactionTotal.toString().plus("€")
            binding.typeOfTransactionIcon.setTransactionImage(item)

        }
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        binding =
            CardTransactionsBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup,
                false
            )
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = currentList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    /** My Diffutil callback for each element in the recyler view */
    class TransactionDiffUtilsCallback : DiffUtil.ItemCallback<Transaction>() {

        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.transactionId == newItem.transactionId
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem

        }




    }





    /*Calculate the transaction income by multiplying sold price with the mass kg */
    fun Transaction.calculateTotal(): Int {
        val result = mass * price

        when (transactionType) {
            "Incomes" -> {
                binding.transactionAmount.setTextColor(Color.GREEN)
            }
            "Expenses" -> {
                binding.transactionAmount.setTextColor(Color.RED)
            }
            else -> {
                binding.transactionAmount.setTextColor(Color.GRAY)
            }
        }
        return result.roundToInt()
    }


}
