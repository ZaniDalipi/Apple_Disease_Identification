package com.zanoapp.applediseaseIdentification.ui.authenticationFirebase

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**We use viewModel factory because we cant crate ViewModel on our own , we need ViewModelProviders utility
 * provided by android to create viewmodels , in this case we use this factory because to our model we pass pass a parameter
 * and the viewmodelprovider utility know only how to create viewmodels with no arg constructor*/
class SignUpViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            return SignUpViewModel(application) as T
        }
        throw IllegalArgumentException("unknown ViewModel class")
    }
}