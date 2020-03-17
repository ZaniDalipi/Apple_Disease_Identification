package com.zanoapp.applediseaseindentificator.uiController.authenticationFirebase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zanoapp.applediseaseindentificator.localDataPersistence.UserRepository

class SignUpViewModelFactory(private val userRepository: UserRepository?) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            return SignUpViewModel(this.userRepository) as T
        }
        throw IllegalArgumentException("unknown ViewModel class")
    }
}