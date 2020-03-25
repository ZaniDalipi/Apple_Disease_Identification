package com.zanoapp.applediseaseIdentification.uiController.authenticationFirebase

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zanoapp.applediseaseIdentification.localDataPersistence.UserRepository

class SignUpViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            return SignUpViewModel(application) as T
        }
        throw IllegalArgumentException("unknown ViewModel class")
    }
}