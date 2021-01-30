package com.zanoapp.applediseaseIdentification.ui.authenticationFirebase

import android.app.Application
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.zanoapp.applediseaseIdentification.R
import com.zanoapp.applediseaseIdentification.databinding.FragmentSignUpBinding
import com.zanoapp.applediseaseIdentification.ui.authenticationFirebase.SignUpViewModel.AuthenticationState.*
import com.zanoapp.applediseaseIdentification.utils.LIFECYCLE_EVENTS
import com.zanoapp.applediseaseIdentification.utils.REQ_ONE_TAP
import com.zanoapp.applediseaseIdentification.utils.TAG_ONE_TAP
import com.zanoapp.applediseaseIdentification.utils.TAG_VIEWMODEL
import kotlinx.android.synthetic.main.fragment_sign_up.*

class SignUpFragment : Fragment() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var signUpViewModel: SignUpViewModel
    private var currentUserAvailable: FirebaseUser? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(LIFECYCLE_EVENTS, "onCreate: Has been called ${Math.random()}")
        super.onCreate(savedInstanceState)
        initViewModel()
        //initGoogleSignInClient()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i(LIFECYCLE_EVENTS, "onCreateView: has been called ${Math.random()}")
        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_sign_up, container, false)
        binding.lifecycleOwner = this

        return binding.root
    }


    private fun userObserver() {

        Log.i(LIFECYCLE_EVENTS, "userObserver: Has been called ${Math.random()}")
        signUpViewModel.user.observe(viewLifecycleOwner, Observer { firebaseUser ->
            firebaseUser?.let { currentUser ->
                currentUserAvailable = currentUser
                Log.i(TAG_VIEWMODEL, "#2 user data : ${currentUserAvailable!!.email}")
                authenticationObserver()
            }
            Log.i(TAG_VIEWMODEL, "userObserverCalled")
        })
    }

    private fun authenticationObserver() {

        Log.i(LIFECYCLE_EVENTS, "authenticationObserver: has been called ${Math.random()} ")
        signUpViewModel.authenticationState.observe(viewLifecycleOwner, Observer { authState ->
            currentUserAvailable = signUpViewModel.user.value
            Log.i(TAG_VIEWMODEL, "authObserverCalled1")
            when (authState) {
                AUTHENTICATED -> {
                    if (findNavController().currentDestination?.id == R.id.signUpFragment) {
                        findNavController().navigate(R.id.action_signUpFragment_to_userProfileDataFragment)
                    }
                    Toast.makeText(
                        activity,
                        "Welcome you have been recognized as ${currentUserAvailable?.email},Enjoy the application",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                UNAUTHENTICATED -> {
                    Toast.makeText(activity, "You are not authenticated", Toast.LENGTH_SHORT).show()
                }
                INVALID_AUTH -> {
                    Toast.makeText(activity, "something went wrong", Toast.LENGTH_SHORT).show()
                }
            }
            Log.i(TAG_VIEWMODEL, "observer value is: ${authState.name}")
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i(LIFECYCLE_EVENTS, "onViewCreated: has been called ${Math.random()}")

        binding.signUpButton.setOnClickListener {
            signUpViewModel.signInWithGoogle(requireActivity())
            progressBar.visibility = View.VISIBLE
        }
        authenticationObserver()
        userObserver()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        signUpViewModel.onActivityResult(requestCode, resultCode, data, requireActivity())
        authenticationObserver()


    }


    /*Scared to delete this method it looks like its useless but iam scared :P */
    private fun initGoogleSignInClient() {
        auth = FirebaseAuth.getInstance()
        val googleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //.setLogSessionId(Math.random().toString())
                .requestIdToken("939177967297-m8t18sqb3436mohhb50rgh9scvdrdcv7.apps.googleusercontent.com")
                .requestEmail()
                .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), googleSignInOptions)
    }

    private fun initViewModel() {
        val application = Application()
        val viewModelFactory = SignUpViewModelFactory(application)
        signUpViewModel = ViewModelProvider(this, viewModelFactory).get(SignUpViewModel::class.java)

    }

}