package com.zanoapp.applediseaseIdentification.ui.camera

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class CameraViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(CameraViewModel::class.java)){
                return CameraViewModel(Application()) as T
            }
        throw IllegalArgumentException("unknown ViewModel class")
    }
}