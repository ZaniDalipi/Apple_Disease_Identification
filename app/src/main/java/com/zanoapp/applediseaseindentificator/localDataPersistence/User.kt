package com.zanoapp.applediseaseindentificator.localDataPersistence

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="users")
data class User(
    @PrimaryKey(autoGenerate = true) var uid:String,
    var name: String,
    var email: String,
    var isAuthenticated: Boolean = false
    )