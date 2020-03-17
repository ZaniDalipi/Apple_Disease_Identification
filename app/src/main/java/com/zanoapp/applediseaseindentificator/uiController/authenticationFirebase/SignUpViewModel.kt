package com.zanoapp.applediseaseindentificator.uiController.authenticationFirebase

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facebook.CallbackManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.zanoapp.applediseaseindentificator.R
import com.zanoapp.applediseaseindentificator.localDataPersistence.User
import com.zanoapp.applediseaseindentificator.localDataPersistence.UserRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class SignUpViewModel(private val userRepository: UserRepository?) : ViewModel() {


    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private var callbackManager: CallbackManager? = null

    private val _toast = MutableLiveData<String>()
    val toast: LiveData<String>
        get() = _toast

    private val _spinner = MutableLiveData<Boolean>()
    val spinner: LiveData<Boolean>
        get() = _spinner

    private val _user = MutableLiveData<FirebaseUser>()
    val user: LiveData<FirebaseUser>
        get() = _user

    private val _userState = MutableLiveData<Boolean>()
    val userState: LiveData<Boolean>
        get() = _userState

    fun signInWithGoogle(activity: Activity) {

        launchDataLoad {
            callbackManager = CallbackManager.Factory.create()

            val googleSignInOptions =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(R.string.default_web_client_id.toString())
                    .requestEmail()
                    .build()

            googleSignInClient = GoogleSignIn.getClient(activity, googleSignInOptions)

            val googleDialogIntent = googleSignInClient.signInIntent
            startActivityForResult(
                activity,
                googleDialogIntent,
                RC_SIGN_IN,
                null
            )
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount, activity: Activity) {
        viewModelScope.launch {
            Log.d(TAG, "firebaseAuthWithGoogle: ${account.id}")
            val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credentials)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            Activity(),
                            "Successfully signed in : ${account.email}",
                            Toast.LENGTH_SHORT
                        ).show()
                        val user = auth.currentUser
                        _user.value = user
                        Toast.makeText(
                            Activity(),
                            "Welcome to my app : ${user?.email}",
                            Toast.LENGTH_LONG
                        ).show()
                        _user.value?.let { firebaseUser -> insertDataToRoomDB(firebaseUser) }
                        _userState.value = true
                    } else {
                        Toast.makeText(
                            Activity(),
                            "Failed to SignUp With Firebase",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }


    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?, activity: Activity) {
        callbackManager?.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val completedTask = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = completedTask.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!, activity)
            } catch (e: ApiException) {
                Log.e(TAG, "Google sign in failed", e)
            }
        }
    }

    fun signOutUser() {
        viewModelScope.launch {
            _userState.value = false
        }
    }

    fun onToastShown() {
        _toast.value = null
    }


    private fun launchDataLoad(block: suspend () -> Unit): Job {
        return viewModelScope.launch {
            try {
                _spinner.value = true
                block()
            } catch (error: Throwable) {
                _toast.value = error.message
            } finally {
                _spinner.value = false
            }
        }
    }

    private fun insertDataToRoomDB(firebaseUser: FirebaseUser) {

        _user.value = firebaseUser

        viewModelScope.launch {
            val currentUser = User(
                uid = _user.value?.uid!!.toLong(),
                name = _user.value?.displayName!!,
                email = _user.value?.email!!
            )
            userRepository?.insertDataToRoomDb(currentUser)
        }
    }

    companion object {
        const val RC_SIGN_IN = 9001
        const val TAG = "signUpViewModel"
    }
}