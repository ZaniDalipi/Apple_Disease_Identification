package com.zanoapp.applediseaseIdentification.ui.managementAndAnalytics.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zanoapp.applediseaseIdentification.databinding.CardTransactionsBinding
import com.zanoapp.applediseaseIdentification.localDataPersistence.transactionsDB.Transaction
import com.zanoapp.applediseaseIdentification.utils.setTransactionImage
import kotlin.math.roundToInt

class TransactionRecyclerViewAdapterRecyclerView :
    ListAdapter<Transaction, TransactionRecyclerViewAdapterRecyclerView.ViewHolder>(
        TransactionDiffUtilsCallback()
    ) {

    lateinit var binding: CardTransactionsBinding

    inner class ViewHolder(val binding: CardTransactionsBinding) :
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
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
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

        if (transactionType == "Expenses") {
            binding.transactionAmount.setTextColor(Color.RED)
        } else {
            binding.transactionAmount.setTextColor(Color.GREEN)
        }

        return result.roundToInt()
    }


}
