package com.zanoapp.applediseaseIdentification.localDataPersistence.transactionsDB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.zanoapp.applediseaseIdentification.localDataPersistence.userDB.User
import java.text.SimpleDateFormat
import java.util.*


@Entity
data class Transaction (

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "transactionId")
        public val transactionId: Int?,

    @ColumnInfo(name = "transaction_type")
        public val transactionType: String,

    @ColumnInfo(name = "product_name")
        public val productName: String,

    @ColumnInfo(name = "mass")
        public val mass: Int,

    @ColumnInfo(name = "unit_price")
        public val price: Double,

    /*@ColumnInfo(name = "gross_amount")
        public val grossAmount: String,*/

    @ColumnInfo(name = "sale_date")
        public val saleDate: String,

    @ColumnInfo(name = "additional_description")
        public val additionalDescription: String,


    @ColumnInfo(name = "client_name")
        public val clientName: String
    )
