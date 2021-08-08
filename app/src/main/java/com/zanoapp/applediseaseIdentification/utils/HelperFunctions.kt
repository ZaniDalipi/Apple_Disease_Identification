package com.zanoapp.applediseaseIdentification.utils


import android.widget.ImageView
import com.zanoapp.applediseaseIdentification.R
import com.zanoapp.applediseaseIdentification.localDataPersistence.transactionsDB.Transaction
import java.nio.FloatBuffer

operator fun ClosedRange<FloatBuffer>.iterator() =
    object : Iterator<FloatBuffer> {
        var current = start
        override fun hasNext(): Boolean {
            return current < endInclusive
        }

        override fun next(): FloatBuffer {
            return current.inc(current)
        }


    }

private operator fun FloatBuffer.inc(input: FloatBuffer): FloatBuffer {
    return inc(this)

}




fun <T> Iterator<T>.customEach(action: (T) -> Unit) {
    for(element: T in this)
        action(element)
}

fun ImageView.setTransactionImage(transaction: Transaction) {
    setImageResource(
        when (transaction.transactionType) {
            "Incomes" -> R.drawable.ic_transaction_plus
            "Expenses" -> R.drawable.ic_transaction_minus
            else -> R.drawable.leaf_48px
        }
    )
}






