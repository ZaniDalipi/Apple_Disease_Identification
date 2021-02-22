package com.zanoapp.applediseaseIdentification.ui.authenticationFirebase

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
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.zanoapp.applediseaseIdentification.localDataPersistence.User
import com.zanoapp.applediseaseIdentification.localDataPersistence.UserDatabase
import com.zanoapp.applediseaseIdentification.localDataPersistence.UserRepository
import com.zanoapp.applediseaseIdentification.utils.*
import kotlinx.coroutines.*


class SignUpViewModel(application: Application) : AndroidViewModel(application) {

    enum class AuthenticationState {
        AUTHENTICATED,
        UNAUTHENTICATED,
        INVALID_AUTH
    }

    private val userRepository = UserRepository(UserDatabase.getInstance(application))
    var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var googleSignInClient: GoogleSignInClient
    private var callbackManager: CallbackManager? = CallbackManager.Factory.create()
    lateinit var credential: AuthCredential

    /** Live Data authentication data holder that will monitor the state of user (its state can be AUTHENTICATED, UNAUTHENTICATED, INVALID_AUTH*/
    val _authenticationState = MutableLiveData<AuthenticationState>()
    val authenticationState: LiveData<AuthenticationState> = _authenticationState


    private val _showOneTapUI = MutableLiveData(true)
    var showOneTapUI: LiveData<Boolean> = _showOneTapUI


    /**Live Data firebaseUser object,holding the latest user*/
    private val _user = MutableLiveData<FirebaseUser>()
    val user: LiveData<FirebaseUser>
        get() = _user

    /**Live Data User object data holder from DB*/
    private val _userObjectFromDb = MutableLiveData<User>()
    val userObjectFromDb: LiveData<User>
        get() = _userObjectFromDb

    /** Helpful object to track the state of the user(assigning to Room isAuthenticated)*/
    val _userState = MutableLiveData<Boolean>()


    init {
        _user.value = firebaseAuth.currentUser
        viewModelScope.launch { getUserFromDb() }
    }


    /**Function that will be triggered after sign up button is clicked and the intention of this function is to initialize the needed objects to request an intent to the user ,and make sure to communicate with firebase backend server through defining the options */
    fun signInWithGoogle(activity: Activity) {
        val googleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(API_KEY)
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
            credential = GoogleAuthProvider.getCredential(account.idToken, null)
            firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _user.value = firebaseAuth.currentUser
                    _authenticationState.value = AuthenticationState.AUTHENTICATED
                    _userState.value = true
                    _user.value?.let { insertDataToRoomDB(it) }
                    Log.i(
                        TAG_VIEWMODEL,
                        "firebaseAuthWithGoogle:  the state of autheticationStateUser is : " + authenticationState.value
                    )
                    Log.i(TAG_VIEWMODEL, "firebaseAuthWithGoogle (account ID): ${account.id}")
                    Log.i(
                        TAG_VIEWMODEL,
                        "user_value(firebaseAuthWithGoogleFun): ${_user.value?.email}"
                    )
                    Log.i(TAG_VIEWMODEL, "user_obj(room): ${_userObjectFromDb.value?.email}")
                    Log.i(
                        TAG_VIEWMODEL,
                        "auth_state_value(firebaseAuthWithGoogleFun): ${authenticationState.value}"
                    )
                    Toast.makeText(activity, "Welcome: ${_user.value?.email}", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    _authenticationState.value = AuthenticationState.INVALID_AUTH
                    _userState.value = false
                    Toast.makeText(activity, "Failed to SignUp With Firebase", Toast.LENGTH_LONG)
                        .show()
                }
            }

        }
    }

    /** Function that will get the user that is last inserted in the Room Db and return that user for future usage in the app*/
    private fun getUserFromDb() = viewModelScope.launch {
        try {
            _userObjectFromDb.value = userRepository.getUserFromDb(_user.value?.uid!!)
        } catch (e: NullPointerException) {
            Log.i(TAG_VIEWMODEL, "User not found(Local DB) ${e.message}")
        }
    }

    /** Function that will insert the user that is signed up after the intent is finished and the sign up has been successfully */
    private fun insertDataToRoomDB(firebaseUser: FirebaseUser) {
        _user.value = firebaseUser
        Log.i(TAG_VIEWMODEL, "user_value(inserting to db): ${_user.value?.email}")
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

        when (requestCode) {
            REGISTRATION_SIGN_IN_CODE -> {
                val completedTask = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = completedTask.getResult(ApiException::class.java)
                    Log.i(
                        TAG_VIEWMODEL,
                        "checked authentication inside onActivityResult(ViewModel) : ${authenticationState.value} "
                    )
                    firebaseAuthWithGoogle(account!!, activity)
                } catch (e: ApiException) {
                    Log.e(TAG_VIEWMODEL, "Google sign in failed", e)
                }
            }
            /*           REQ_ONE_TAP -> {
                           try {
                               val credentials = oneTapClient.getSignInCredentialFromIntent(data)
                               val idToken = credentials.googleIdToken
                               val username = credentials.id
                               val password = credentials.password

                               when {
                                   idToken != null -> {
                                       val accountTask = GoogleSignIn.getSignedInAccountFromIntent(data)
                                       val account = accountTask.getResult(ApiException::class.java)
                                       account?.let { firebaseAuthWithGoogle(it, Activity()) }
                                       Log.d(TAG_ONE_TAP, "Got ID token.")
                                   }
                                   password != null -> {
                                       // auth with backend
                                       Log.d(TAG_ONE_TAP, "Got ID token.")

                                   }
                                   else -> {
                                       Log.d(TAG_ONE_TAP, "Got ID token and password.")
                                   }
                               }
                           } catch (e: ApiException) {
                               Log.e(TAG_ONE_TAP, "one tap Google sign in failed", e)
                           }
                       }*/

        }
    }


    /** function that will check if a user is authenticated in the beginning of starting the application*/
    fun checkIfUserIsAuthenticated() {
        if (_user.value != null) {
            _authenticationState.value = AuthenticationState.AUTHENTICATED
            _userState.value = true
        } else {
            _authenticationState.value = AuthenticationState.UNAUTHENTICATED
            _userState.value = false
        }
    }

   /* fun loginWithFirebase(email: String, password: String) {

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    // succesfully logged in
                    _user.value = firebaseAuth.currentUser
                    _authenticationState.value = AuthenticationState.AUTHENTICATED
                } else {
                    Toast.makeText(
                        getApplication(),
                        "Login Failure" + it.exception,
                        Toast.LENGTH_SHORT
                    ).show()
                }

        }
    }

    fun registerWithFirebase(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _user.postValue(firebaseAuth.currentUser)
                    _authenticationState.value = AuthenticationState.AUTHENTICATED
                } else {
                    Toast.makeText(
                        getApplication(),
                        "Register Failure" + task.exception,
                        Toast.LENGTH_SHORT
                    ).show()

                }

            }
    }*/
}