package com.zanoapp.applediseaseIdentification.localDataPersistence.userDB

import kotlinx.coroutines.*

class UserRepository(private val userDb: UserDatabase) {

    fun insertDataToRoomDb(user: User) {
        CoroutineScope(Dispatchers.IO).launch {
            userDb.userDao().insertUser(user)
        }
    }

    suspend fun getUserFromDb(userId: String): User? =
        supervisorScope {
            val deferredResult = async(Dispatchers.IO) {
                userDb.userDao().getUser(userId)
            }
            deferredResult.await()
        }
}