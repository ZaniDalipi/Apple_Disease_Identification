package com.zanoapp.applediseaseindentificator.utils

import java.lang.Exception


sealed class MyResult{

    data class Success<out T>(val data: T) : MyResult() // Status success and data of the result
    data class Error(val exception: Exception) : MyResult() // Status Error an error message
    data class Canceled(val exception: Exception?) : MyResult() // Status Canceled


    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
            is Canceled -> "Canceled[exception=$exception]"
        }
    }
}