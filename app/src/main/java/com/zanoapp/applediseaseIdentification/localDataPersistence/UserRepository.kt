package com.zanoapp.applediseaseIdentification.localDataPersistence

import kotlinx.coroutines.*

class UserRepository(private val userDb: UserDatabase) {


    fun insertDataToRoomDb(user: User) {
        CoroutineScope(Dispatchers.IO).launch {
            userDb.userDao().insertUser(user)
        }
    }

    suspend fun getUserFromDb(uid: String): User? {
        return withContext(Dispatchers.Default) {
            val user = userDb.userDao().getUser(uid)
            user
        }
    }
}