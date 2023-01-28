package com.zanoapp.applediseaseIdentification.utils

import android.content.Context
import android.content.res.Configuration
import androidx.lifecycle.ViewModelProvider
import com.zanoapp.applediseaseIdentification.localDataPersistence.transactionsDB.TransactionDatabase
import com.zanoapp.applediseaseIdentification.localDataPersistence.transactionsDB.TransactionRepository
import com.zanoapp.applediseaseIdentification.ui.managementAndAnalytics.AccountAnalyticsViewModel
import com.zanoapp.applediseaseIdentification.ui.managementAndAnalytics.AccountAnalyticsViewModelFactory

fun isDarkTheme(context: Context): Boolean {
    return context.resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
}



