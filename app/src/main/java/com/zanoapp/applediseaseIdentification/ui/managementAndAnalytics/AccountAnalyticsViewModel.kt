package com.zanoapp.applediseaseIdentification.ui.managementAndAnalytics

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zanoapp.applediseaseIdentification.localDataPersistence.transactionsDB.Transaction
import com.zanoapp.applediseaseIdentification.localDataPersistence.transactionsDB.TransactionRepository
import com.zanoapp.applediseaseIdentification.localDataPersistence.userDB.User
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AccountAnalyticsViewModel(private val transactionRepository: TransactionRepository) :
    ViewModel() {


    lateinit var transaction: Transaction

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


    /*  @RequiresApi(Build.VERSION_CODES.N)
      fun insertTransactionIntoDB(){
          transaction = Transaction(
              tId = "implement an function to generate the name + number + client",
              transactionType = "this is the dropdown option ",
              productName = "the product name specified in edittext",
              mass = "the mass sold in kg/l".toInt(),
              price = "price of product".toDouble(),
              grossAmount = "the amount that the farmer got",
              saleDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault(Locale.Category.FORMAT)),
              additionalDescription = "get the data from the editText",
              user = User("", "null", "null", false),
              clientName = "get the client name added by the user"
          )

          transactionRepository.insertTransaction(transaction)
      }

  */

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

    fun getTransactionByDateRange(startdate: SimpleDateFormat, endDate: SimpleDateFormat){
        transactionRepository.getTransactionByDateRange(startdate, endDate)
    }


}