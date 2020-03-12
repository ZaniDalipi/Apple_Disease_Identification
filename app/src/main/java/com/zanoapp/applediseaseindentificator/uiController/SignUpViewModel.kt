package com.zanoapp.applediseaseindentificator.uiController

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import com.zanoapp.applediseaseindentificator.localDataPersistence.UserRepository
import kotlinx.coroutines.launch


private val RC_SIGN_IN = 1
private val TAG = "signUpViewModel"

class SignUpViewModel(private var userRepository: UserRepository) : ViewModel() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient


    /**
     * 1. create an object for user info
     * 2. create an object for user state
     * 3. create toast livedata
     * 4. create spinner livedata */

    private val _toast = MutableLiveData<String>()
    val toast: LiveData<String>
        get() = _toast

    private val _spinner = MutableLiveData<String>()
    val spinner: LiveData<String>
        get() = _spinner

    private val _user = MutableLiveData<FirebaseUser>()
    val user: LiveData<FirebaseUser>
        get() = _user

    private val _userState = MutableLiveData<Boolean>()
    val userState: LiveData<Boolean>
        get() = _userState


    init {
        signInWithGoogle(Activity())

    }

     fun signInWithGoogle(activity: Activity) {

        auth = FirebaseAuth.getInstance()

        val googleSigninOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(R.string.default_web_client_id.toString())
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(Activity(), googleSigninOptions)

        val googleDialogIntent = googleSignInClient.signInIntent
        activity.startActivityForResult(googleDialogIntent, RC_SIGN_IN)
    }


     fun handleSignInResult(completedTask: Task<GoogleSignInAccount>, activity: Activity) {
        viewModelScope.launch {
            try {
                val account = completedTask.getResult(ApiException::class.java)
                account.let {
                    val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
                    auth.signInWithCredential(credential).addOnCompleteListener {
                        if (completedTask.isSuccessful) {
                            Log.d(TAG, "signInWithCredential:success")
                            Toast.makeText(activity.applicationContext, "Sign In Successful ${auth.currentUser?.email}", Toast.LENGTH_SHORT).show()
                            _user.value = auth.currentUser
                        }
                        /* when (val result = userRepository.signInWithCredential(credential)) {
                         is MyResult.Success<*> -> {
                             result.data.

                         }
                         is MyResult.Error -> {
                             TODO()
                         }
                         is MyResult.Canceled -> {
                             TODO()

                         }
                         else -> TODO()
                     }*/
                    }
                }
            } catch (e: ApiException) {
                Toast.makeText(activity.applicationContext, "Sign In Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun checkUserLoggedIn(): FirebaseUser? {
        var _firebaseUser: FirebaseUser? = null
        viewModelScope.launch {
            _firebaseUser = userRepository.checkUserLoggedIn()
        }
        return _firebaseUser
    }

    fun logOutUser(){
        viewModelScope.launch {
            userRepository.logOutUser()
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?, activity: Activity)
    {

        onActivityResult(requestCode, resultCode, data, activity)

        if(requestCode == RC_SIGN_IN)
        {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task, activity)
        }
    }
}