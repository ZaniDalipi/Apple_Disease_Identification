package com.zanoapp.applediseaseindentificator.models

data class SavedLocation(
    private val locationId: String,
    private val locationLat: Float,
    private val locationLong: Float,
    private val diseaseName: String,
    private val diseaseImage: Int
)