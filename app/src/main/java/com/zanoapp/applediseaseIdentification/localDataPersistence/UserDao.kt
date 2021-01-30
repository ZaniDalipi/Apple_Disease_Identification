package com.zanoapp.applediseaseIdentification.localDataPersistence

import androidx.room.*

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(vararg users: User)


    @Query("SELECT * FROM users WHERE users.uid = :uid  LIMIT 1")
    fun getUser(uid: String): User

}