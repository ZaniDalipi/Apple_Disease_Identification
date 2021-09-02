package com.zanoapp.applediseaseIdentification.ui.managementAndAnalytics

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zanoapp.applediseaseIdentification.localDataPersistence.transactionsDB.TransactionRepository
import com.zanoapp.applediseaseIdentification.ui.authenticationFirebase.SignUpViewModel

@Suppress("UNCHECKED_CAST")
class AccountAnalyticsViewModelFactory(private val transactionRepository: TransactionRepository) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccountAnalyticsViewModel::class.java)) {
            return AccountAnalyticsViewModel(transactionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
