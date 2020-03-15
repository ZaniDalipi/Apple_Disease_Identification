package com.zanoapp.applediseaseindentificator.localDataPersistence

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.auth.User
import com.squareup.okhttp.Dispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface UserRepository {


    suspend fun insertDataToRoomDb(user: User) {
        withContext(Dispatchers.IO) {

        }
    }

    suspend fun checkUserLoggedIn(): FirebaseUser?
    suspend fun logOutUser()

    suspend fun signInWithCredential(
        authCredential: AuthCredential
    ): Result<AuthResult?>
}