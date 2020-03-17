package com.zanoapp.applediseaseindentificator.localDataPersistence

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import kotlinx.coroutines.withContext
class UserRepository(private val userDb: UserDatabase) {


    fun insertDataToRoomDb(user: User) {
        CoroutineScope(Dispatchers.IO).launch {
            userDb.userDao().insertUser(user)
        }
    }
}