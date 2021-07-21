package com.zanoapp.applediseaseIdentification.localDataPersistence.transactionsDB

import androidx.lifecycle.LiveData
import androidx.room.*
import java.text.SimpleDateFormat
import java.util.*

@Dao
interface TransactionDAO {

    /** Insert a transaction into the DB */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insertTransaction(transaction: Transaction)

    /** Delete a transaction */
    @Delete
    fun deleteTransaction(transaction: Transaction)

    /** Get all the transaction that user has done */
    @Query("Select * FROM `Transaction`")
        suspend fun getAllTransactions(): List<Transaction>

    /** get all transaction where the type is incomes, it means that the user has got paid on a product*/
    @Query("SELECT * FROM `Transaction` WHERE transaction_type == 'incomes'")
        fun getIncomesTransaction(): List<Transaction>

    /** get all transaction where the type is expenses, it means that the user has paid out*/
    @Query("SELECT * FROM `Transaction` WHERE transaction_type == 'expenses'")
        fun getExpensesTransaction(): List<Transaction>

    /** Get transactions by meeting a certain condition of the client name */
    @Query("SELECT * FROM `transaction` WHERE client_name LIKE :clientName")
        fun getTransactionByClient(clientName: String): List<Transaction>

    /** Get data by a range of date */
    @Query("SELECT * FROM `Transaction` WHERE sale_date BETWEEN :startDate AND :endDate")
        fun getTransactionByDataRange(startDate: Date, endDate: Date): LiveData<List<Transaction>>
}