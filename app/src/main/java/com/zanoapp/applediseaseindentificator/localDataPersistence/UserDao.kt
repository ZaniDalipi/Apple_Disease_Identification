package com.zanoapp.applediseaseindentificator.localDataPersistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Insert
    fun insertUser(user: User)

}