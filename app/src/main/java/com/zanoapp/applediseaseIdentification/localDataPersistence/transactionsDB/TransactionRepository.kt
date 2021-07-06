package com.zanoapp.applediseaseIdentification.localDataPersistence.transactionsDB

import kotlinx.coroutines.*
import java.text.SimpleDateFormat

class TransactionRepository(transactionDatabase: TransactionDatabase) {

    private val transactionDB = transactionDatabase.transactionDao()

    /** Inset a transaction into the DB*/
    fun insertTransaction(transaction: Transaction) {
        CoroutineScope(Dispatchers.IO).launch {
            transactionDB.insertTransaction(transaction)
        }
    }

    /** Delete a transaction from the DB*/
    fun deleteTransaction(transaction: Transaction) {
        CoroutineScope(Dispatchers.IO).launch {
            transactionDB.deleteTransaction(transaction)
        }
    }

    /** Get all the transaction that user has done */
    fun getAllTransactions(): List<Transaction> {
        var myTransactions = listOf<Transaction>()
        CoroutineScope(Dispatchers.IO).launch {
            myTransactions = transactionDB.getAllTransactions()
        }
        return myTransactions
    }

    /** get all transaction where the type is incomes, it means that the user has got paid on a product*/
    suspend fun getIncomesTransaction(): List<Transaction> {
        return supervisorScope {
            val deferredResult = async(Dispatchers.IO) {
                transactionDB.getIncomesTransaction()
            }
            deferredResult.await()
        }
    }

    /** get all transaction where the type is expenses, it means that the user has paid out*/
    suspend fun getExpenseTransaction(): List<Transaction> {
        return supervisorScope {
            val deferredResult = async(Dispatchers.IO) {
                transactionDB.getExpensesTransaction()
            }
            deferredResult.await()
        }
    }

    /** Get transactions by meeting a certain condition of the client name */
    fun getTransactionByClient(clientName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            transactionDB.getTransactionByClient(clientName)
        }
    }

    /** Get data by a range of date */
    fun getTransactionByDateRange(startDate: SimpleDateFormat, endDate: SimpleDateFormat) {
        CoroutineScope(Dispatchers.IO).launch {
            transactionDB.getTransactionByDataRange(startDate, endDate)
        }
    }
}