package com.zanoapp.applediseaseIdentification.utils

import android.widget.ImageView
import com.zanoapp.applediseaseIdentification.R
import com.zanoapp.applediseaseIdentification.localDataPersistence.transactionsDB.Transaction

fun ImageView.setTransactionImage(transaction: Transaction) {
    setImageResource(
        when (transaction.transactionType) {
            "Incomes" -> R.drawable.ic__01_receive_amount
            "Expenses" -> R.drawable.ic__02_sending_amount
            else -> R.drawable.leaf_48px
        }
    )
}