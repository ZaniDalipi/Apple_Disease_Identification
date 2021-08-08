package com.zanoapp.applediseaseIdentification.localDataPersistence.transactionsDB

import kotlinx.coroutines.*
import java.util.*

class TransactionRepository(private val transactionDAO: TransactionDAO) {
    

    /** Inset a transaction into the DB*/
    fun insertTransaction(transaction: Transaction) {
        CoroutineScope(Dispatchers.IO).launch {
            transactionDAO.insertTransaction(transaction)
        }
    }

    /** Delete a transaction from the DB*/
    fun deleteTransaction(transaction: Transaction) {
        CoroutineScope(Dispatchers.IO).launch {
            transactionDAO.deleteTransaction(transaction)
        }
    }

    /** Get all the transaction that user has done */
    fun getAllTransactions(): List<Transaction> {
        var myTransactions = listOf<Transaction>()
        CoroutineScope(Dispatchers.IO).launch {
            myTransactions = transactionDAO.getAllTransactions()
        }
        return myTransactions
    }

    /** get all transaction where the type is incomes, it means that the user has got paid on a product*/
    suspend fun getIncomesTransaction(): List<Transaction> {
        return supervisorScope {
            val deferredResult = async(Dispatchers.IO) {
                transactionDAO.getIncomesTransaction()
            }
            deferredResult.await()
        }
    }

    /** get all transaction where the type is expenses, it means that the user has paid out*/
    suspend fun getExpenseTransaction(): List<Transaction> {
        return supervisorScope {
            val deferredResult = async(Dispatchers.IO) {
                transactionDAO.getExpensesTransaction()
            }
            deferredResult.await()
        }
    }

    /** Get transactions by meeting a certain condition of the client name */
    fun getTransactionByClient(clientName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            transactionDAO.getTransactionByClient(clientName)
        }
    }

    /** Get data by a range of date */
    fun getTransactionByDateRange(startDate: String, endDate: String) {
        CoroutineScope(Dispatchers.IO).launch {
            transactionDAO.getTransactionByDataRange(startDate, endDate)
        }
}
}