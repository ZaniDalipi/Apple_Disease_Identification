package com.zanoapp.applediseaseIdentification.utils

import androidx.room.TypeConverter
import com.google.android.gms.common.internal.Constants
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object Converters {
    @TypeConverter
    fun fromTimestamp(value: String?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}