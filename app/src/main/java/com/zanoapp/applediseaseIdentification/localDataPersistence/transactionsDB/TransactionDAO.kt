package com.zanoapp.applediseaseIdentification.localDataPersistence.transactionsDB

import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.*

@Dao
interface TransactionDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTransaction(transaction: Transaction)

    @Query("Delete from `Transaction` where transactionId == :transactionId")
    fun deleteTransaction(transactionId: Long)

    @Update
    fun updateTransaction(transaction: Transaction)


    @Query("Select * FROM `Transaction` ORDER BY transactionId DESC")
    fun getAllTransactions(): List<Transaction>


    @Query("SELECT * FROM `Transaction` WHERE transaction_type == 'Incomes' ORDER BY transactionId DESC")
    fun getIncomesTransaction(): List<Transaction>


    @Query("SELECT * FROM `Transaction` WHERE transaction_type == 'Expenses' ORDER BY transactionId DESC")
    fun getExpensesTransaction(): List<Transaction>


    @Query("SELECT * FROM `transaction` WHERE client_name LIKE :clientName")
    fun getTransactionByClient(clientName: String): List<Transaction>


    @Query("SELECT * FROM `Transaction` WHERE sale_date BETWEEN :startDate AND :endDate")
    fun getTransactionByDataRange(startDate: String, endDate: String): LiveData<List<Transaction>>
}