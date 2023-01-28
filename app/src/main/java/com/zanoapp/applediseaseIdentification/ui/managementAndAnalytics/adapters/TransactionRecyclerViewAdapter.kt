package com.zanoapp.applediseaseIdentification.ui.managementAndAnalytics.adapters

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
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


    inner class ViewHolder :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Transaction) {

            val transactionTotal = item.calculateTotal()

            binding.apply {
                productNameTextView.text = item.productName
                transactionDate.text = item.saleDate
                transactionAmount.text = transactionTotal.toString().plus("€")
                typeOfTransactionIcon.setTransactionImage(item)
            }
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
        val item = getItem(position)
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    /** My Diffutils callback for each element in the recycler view */
    class TransactionDiffUtilsCallback : DiffUtil.ItemCallback<Transaction>() {

        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            Log.i("DebuggingAppAdapter", "areItemsTheSame Called ()")
            return oldItem.transactionId == newItem.transactionId
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            Log.i("DebuggingAppAdapter", "areContentsTheSame Called ()")
            return oldItem.describeContents() == newItem.describeContents()
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
