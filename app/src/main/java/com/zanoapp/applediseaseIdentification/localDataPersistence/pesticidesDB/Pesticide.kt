package com.zanoapp.applediseaseIdentification.localDataPersistence.pesticidesDB

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Pesticide(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "pesticide_id")
    public val pesticideId: Int,

    @ColumnInfo(name = "pesticide_name")
    public val pesticideName: String,

    @ColumnInfo(name = "pesticide_type")
    public val pesticideType: String,

    @ColumnInfo(name = "pesticide_company")
    public val pesticideCompany: String,

    @ColumnInfo(name = "pesticide_ingredients")
    public val pesticideIngredients: String,

    @ColumnInfo(name = "pesticide_description")
    public val pesticideDescription: String,

    @ColumnInfo(name = "pesticide_is_it_organic")
    public val isItOrganic: String,

    @ColumnInfo(name = "pesticide_usage_dose")
    public val pesticideUsage: String
) : Parcelable {
    constructor(
        pesticideName: String,
        pesticideType: String,
        pesticideCompany: String,
        pesticideIngredients: String,
        pesticideDescription: String,
        isItOrganic: String,
        pesticideUsage: String
    ) : this (
        0,
        pesticideName,
        pesticideType,
        pesticideCompany,
        pesticideIngredients,
        pesticideDescription,
        isItOrganic,
        pesticideUsage
    )
}
