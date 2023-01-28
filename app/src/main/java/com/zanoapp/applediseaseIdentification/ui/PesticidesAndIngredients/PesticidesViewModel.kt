package com.zanoapp.applediseaseIdentification.ui.PesticidesAndIngredients

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zanoapp.applediseaseIdentification.localDataPersistence.pesticidesDB.Pesticide

class PesticidesViewModel : ViewModel() {

    private val _listOfPesticide = MutableLiveData<Pesticide>()
    val listOfPesticide : LiveData<Pesticide>
        get() = _listOfPesticide


    fun getAllPesticides() {

    }
    
}