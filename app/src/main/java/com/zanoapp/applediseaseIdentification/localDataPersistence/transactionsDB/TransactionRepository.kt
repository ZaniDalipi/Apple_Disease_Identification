package com.zanoapp.applediseaseIdentification.localDataPersistence.transactionsDB

import kotlinx.coroutines.*

class TransactionRepository(private val transactionDAO: TransactionDAO) {


    fun insertTransaction(transaction: Transaction) {
        CoroutineScope(Dispatchers.IO).launch {
            transactionDAO.insertTransaction(transaction)
        }
    }

    fun deleteTransaction(transactionId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            transactionDAO.deleteTransaction(transactionId)
        }
    }

    fun updateTransaction(transaction: Transaction) {
        CoroutineScope(Dispatchers.IO).launch {
            transactionDAO.updateTransaction(transaction)
        }
    }

    suspend fun getAllTransactions(): List<Transaction> {
        return supervisorScope {
            val deferredResult = async(Dispatchers.IO) {
                transactionDAO.getAllTransactions()
            }
            deferredResult.await()
        }
    }

    suspend fun getIncomesTransaction(): List<Transaction> {
        return supervisorScope {
            val deferredResult = async(Dispatchers.IO) {
                transactionDAO.getIncomesTransaction()
            }
            deferredResult.await()
        }
    }

    suspend fun getExpenseTransaction(): List<Transaction> {
        return supervisorScope {
            val deferredResult = async(Dispatchers.IO) {
                transactionDAO.getExpensesTransaction()
            }
            deferredResult.await()
        }
    }

    fun getTransactionByClient(clientName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            transactionDAO.getTransactionByClient(clientName)
        }
    }

    fun getTransactionByDateRange(startDate: String, endDate: String) {
        CoroutineScope(Dispatchers.IO).launch {
            transactionDAO.getTransactionByDataRange(startDate, endDate)
        }
    }
}