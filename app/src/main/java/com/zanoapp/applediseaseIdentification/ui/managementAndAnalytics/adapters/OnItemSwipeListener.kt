package com.zanoapp.applediseaseIdentification.ui.managementAndAnalytics.adapters

sealed interface OnItemSwipeListener {

    interface onDeleteCardItemClickListener {

        fun deleteTransaction(position: Int)


    }

    interface onEditCardItemClickListener {
        fun editTransaction(position: Int)
    }
    
}