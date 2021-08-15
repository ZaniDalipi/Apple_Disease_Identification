package com.zanoapp.applediseaseIdentification.ui.managementAndAnalytics

import androidx.lifecycle.*
import com.zanoapp.applediseaseIdentification.localDataPersistence.transactionsDB.Transaction
import com.zanoapp.applediseaseIdentification.localDataPersistence.transactionsDB.TransactionRepository
import kotlinx.coroutines.launch

class AccountAnalyticsViewModel(private val transactionRepository: TransactionRepository) :
    ViewModel() {

    private val _currentBalance = MutableLiveData<Int>()
    val currentBalance: LiveData<Int>
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


    fun insertTransactionIntoDB(transaction: Transaction) {
        transactionRepository.insertTransaction(transaction)
    }


    fun deleteTransaction(transactionId: Long) {
        transactionRepository.deleteTransaction(transactionId)
    }

    fun getAllTransactions() {
        viewModelScope.launch {
            _transactions.value = transactionRepository.getAllTransactions()
        }
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
        _transactions.value = transactions.value
        getAllTransactions()
        getIncomeTransactions()
        getExpenseTransactions()
    }
}