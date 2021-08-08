package com.zanoapp.applediseaseIdentification.ui.managementAndAnalytics

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.zanoapp.applediseaseIdentification.localDataPersistence.transactionsDB.Transaction
import com.zanoapp.applediseaseIdentification.localDataPersistence.transactionsDB.TransactionDatabase
import com.zanoapp.applediseaseIdentification.localDataPersistence.transactionsDB.TransactionRepository
import com.zanoapp.applediseaseIdentification.localDataPersistence.userDB.UserDatabase
import com.zanoapp.applediseaseIdentification.localDataPersistence.userDB.UserRepository
import com.zanoapp.applediseaseIdentification.utils.TransactionMockData
import kotlinx.coroutines.launch
import java.util.*

class AccountAnalyticsViewModel(private val transactionRepository: TransactionRepository) : ViewModel() {

    private val _currentBalance = MutableLiveData<Long>()
    val currentBalance: LiveData<Long>
        get() = _currentBalance

    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>>
        get() = _transactions

    private val _incomeTransactions = MutableLiveData<List<Transaction>>()
    val incomeTransactions: LiveData<List<Transaction>>
        get() = _incomeTransactions

    private val _expensesTransactions = MutableLiveData<List<Transaction>>()
    val expensesTransactions: LiveData<List<Transaction>>
        get() = _expensesTransactions


      fun insertTransactionIntoDB(transaction: Transaction){
          transactionRepository.insertTransaction(transaction)
      }


    fun deleteTransaction(transaction: Transaction) {
        transactionRepository.deleteTransaction(transaction)
    }

    fun getAllTransactions() {
        _transactions.value = transactionRepository.getAllTransactions()
    }


    fun getIncomeTransactions() {
        viewModelScope.launch {
            _incomeTransactions.value = transactionRepository.getIncomesTransaction()
        }
    }


    fun getExpenseTransactions() {
        viewModelScope.launch {
            _expensesTransactions.value = transactionRepository.getExpenseTransaction()
        }
    }

    fun getTransactionByClient(clientName: String) {
        transactionRepository.getTransactionByClient(clientName)
    }

    fun getTransactionByDateRange(startDate: String, endDate: String) {
        transactionRepository.getTransactionByDateRange(startDate, endDate)
    }

    init {
        getAllTransactions()
    }
}