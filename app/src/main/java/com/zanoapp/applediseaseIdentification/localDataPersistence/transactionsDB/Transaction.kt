package com.zanoapp.applediseaseIdentification.localDataPersistence.transactionsDB

import android.os.Parcelable
import androidx.databinding.adapters.Converters
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Transaction(
    @PrimaryKey(autoGenerate = true) val transactionId: Long,

    @ColumnInfo(name = "transaction_type") val transactionType: String,

    @ColumnInfo(name = "product_name") val productName: String,

    @ColumnInfo(name = "mass") val mass: Int,

    @ColumnInfo(name = "unit_price") val price: Double,

    @ColumnInfo(name = "sale_date")
    @TypeConverters(Converters::class) val saleDate: String,

    @ColumnInfo(name = "additional_description") val additionalDescription: String,

    @ColumnInfo(name = "client_name") val clientName: String
) : Parcelable {

    constructor(
        transactionType: String,
        productName: String,
        mass: Int,
        price: Double,
        saleDate: String,
        additionalDescription: String,
        clientName: String
    )
            : this(
        0,
        transactionType,
        productName,
        mass,
        price,
        saleDate,
        additionalDescription,
        clientName
    )
}
