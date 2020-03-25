package com.zanoapp.applediseaseIdentification.uiController.authenticationFirebase

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.facebook.CallbackManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.zanoapp.applediseaseIdentification.localDataPersistence.User
import com.zanoapp.applediseaseIdentification.localDataPersistence.UserDatabase
import com.zanoapp.applediseaseIdentification.localDataPersistence.UserRepository
import kotlinx.coroutines.*


class SignUpViewModel(application: Application) : AndroidViewModel(application) {

    enum class AuthenticationState {
        UNAUTHENTICATED,
        AUTHENTICATED,
        INVALID_AUTH
    }

    private val userRepository = UserRepository(UserDatabase.getInstance(application))
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var googleSignInClient: GoogleSignInClient
    private var callbackManager: CallbackManager? = CallbackManager.Factory.create()

    /** Live Data authentication data holder that will monitor the state of user*/
    val _authenticationState = MutableLiveData<AuthenticationState>()
    val authenticationState : LiveData<AuthenticationState>
        get() = _authenticationState

    /**Live Data User object data holder from DB*/
    private val _userObjectFromDb = MutableLiveData<User>()


    /**Live Data firebaseUser object,holding the latest user*/
    private val _user = MutableLiveData<FirebaseUser>()
    val user: LiveData<FirebaseUser>
        get() = _user

    /** Helpful object to track the state of the user(assigning to Room isAuthenticated)*/
    private val _userState = MutableLiveData<Boolean>()


    init {
        _user.value = auth.currentUser
        checkIfUseIsAuthenticated()

        Log.i(TAG, "_user_value(init): ${_user.value?.email}")
      /*  viewModelScope.launch {getUserFromDb()}*/
    }

    /**Function that will be triggered after sign up button is clicked and the intention of this function is to initialize the needed objects to request an intent to the user ,and make sure to communicate with firebase backend server through defining the options */
    fun signInWithGoogle(activity: Activity) {
            val googleSignInOptions =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken("939177967297-qtssncnkb41vqps404s5tq7onceocrph.apps.googleusercontent.com")
                    .requestEmail()
                    .build()

            googleSignInClient = GoogleSignIn.getClient(activity, googleSignInOptions)
            val googleDialogIntent = googleSignInClient.signInIntent

            activity.startActivityForResult(
                googleDialogIntent,
                REGISTRATION_SIGN_IN_CODE
            )
        }

    /** Function that will check if the credentials are right , check if  task that has to be executed is successfully executed,
     *  and also provide the app with current user data than can be observed in different circumstances*/
    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount, activity: Activity) {
        viewModelScope.launch {
            Log.i(TAG, "firebaseAuthWithGoogle (account ID): ${account.id}")
            val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credentials).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _user.value = auth.currentUser
                        _authenticationState.value = AuthenticationState.AUTHENTICATED
                        _userState.value = true
                        _user.value?.let {insertDataToRoomDB(it) }

                        Log.i(TAG, "user_value(firebaseAuthWithGoogleFun): ${_user.value?.email}")
                        Toast.makeText(activity,"Welcome: ${_user.value?.email}",Toast.LENGTH_LONG).show()
                    } else {
                        _authenticationState.value = AuthenticationState.INVALID_AUTH
                        _userState.value = false
                        Toast.makeText(activity,"Failed to SignUp With Firebase",Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    /** Function that will get the user that is last inserted in the Room Db and return that user for future usage in the app*/
  /*  private fun getUserFromDb() {
        CoroutineScope(Dispatchers.IO).launch{
            _userObjectFromDb.value = _user.value?.uid?.let { userRepository.getUserFromDb(it) }
        }
    }
*/

    /** Function that will insert the user that is signed up after the intent is finished and the sign up has been successfully */
    private fun insertDataToRoomDB(firebaseUser: FirebaseUser) {
       _user.value = firebaseUser

       // Log.i(TAG, "user_value(inserting to db): ${_user.value?.email}")
        viewModelScope.launch {
            val currentUser = User(
                uid = _user.value?.uid!!,
                name = _user.value?.displayName!!,
                email = _user.value?.email!!,
                isAuthenticated = _userState.value!!
            )
            userRepository.insertDataToRoomDb(currentUser)
        }
    }

    /**Function that will process the intent sent by the user and than calls the onActivityResult that is placed in activity/fragment */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?, activity: Activity) {
        callbackManager?.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REGISTRATION_SIGN_IN_CODE) {
            val completedTask = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = completedTask.getResult(ApiException::class.java)
                Log.i(TAG, "checked authentication inside onActivityResult(ViewModel) : ${_authenticationState.value} ")

                firebaseAuthWithGoogle(account!!, activity)
            } catch (e: ApiException) {
                Log.e(TAG, "Google sign in failed", e)
            }
        }
    }


    /** function that will check if a user is authenticated in the beginning of starting the application*/
    private fun checkIfUseIsAuthenticated() {
        if (_user.value != null) {
            _authenticationState.value = AuthenticationState.AUTHENTICATED
            _userState.value = true
        } else {
            _authenticationState.value = AuthenticationState.UNAUTHENTICATED
            _userState.value = false
        }
    }

    companion object {
        const val REGISTRATION_SIGN_IN_CODE = 9001
        const val TAG = "signUpViewModel"
    }
}