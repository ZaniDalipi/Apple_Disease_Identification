package com.zanoapp.applediseaseIdentification.localDataPersistence.transactionsDB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.zanoapp.applediseaseIdentification.localDataPersistence.userDB.User
import java.text.SimpleDateFormat


@Entity
data class Transaction (

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "transaction_id")
        private val tId: String,

    @ColumnInfo(name = "transaction_type")
        private val transactionType: String,

    @ColumnInfo(name = "product_type")
        private val productName: String,

    @ColumnInfo(name = "mass")
        private val mass: Int,

    @ColumnInfo(name = "unit_price")
        private val price: Double,

    @ColumnInfo(name = "gross_amount")
        private val grossAmount: String,

    @ColumnInfo(name = "sale_date")
        private val saleDate: SimpleDateFormat,

    @ColumnInfo(name = "additional_description")
        private val additionalDescription: String,


    @ColumnInfo(name = "user")
        private val user: User,

    @ColumnInfo(name = "client_name")
        private val clientName: String
    )
