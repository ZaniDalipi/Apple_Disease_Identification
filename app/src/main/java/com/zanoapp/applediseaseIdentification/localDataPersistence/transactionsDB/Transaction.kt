package com.zanoapp.applediseaseIdentification.localDataPersistence.transactionsDB

import androidx.databinding.adapters.Converters
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.sql.Date


@Entity
data class Transaction (
    @PrimaryKey(autoGenerate = true)
        public val transactionId: Long,

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
    @TypeConverters(Converters::class)
        public val saleDate: String,

    @ColumnInfo(name = "additional_description")
        public val additionalDescription: String,


    @ColumnInfo(name = "client_name")
        public val clientName: String
    ) {
    constructor(transactionType: String, productName: String, mass: Int, price: Double, saleDate: String, additionalDescription: String, clientName: String)
            : this(0, transactionType, productName, mass, price, saleDate, additionalDescription, clientName)
}
