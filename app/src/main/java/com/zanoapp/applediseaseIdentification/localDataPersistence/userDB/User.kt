package com.zanoapp.applediseaseIdentification.localDataPersistence.userDB

import androidx.room.Entity
import androidx.room.PrimaryKey

const val CURRENT_USER_ID = "0"

@Entity(tableName="users")
data class User(
    @PrimaryKey(autoGenerate = false)
    var uid:String = CURRENT_USER_ID,
    var name: String,
    var email: String,
    var isAuthenticated: Boolean
    )