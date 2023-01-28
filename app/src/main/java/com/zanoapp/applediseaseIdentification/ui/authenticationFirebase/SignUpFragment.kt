package com.zanoapp.applediseaseIdentification.ui.authenticationFirebase

import android.app.Application
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseUser
import com.zanoapp.applediseaseIdentification.R
import com.zanoapp.applediseaseIdentification.databinding.FragmentSignUpBinding
import com.zanoapp.applediseaseIdentification.ui.authenticationFirebase.SignUpViewModel.AuthenticationState.*
import com.zanoapp.applediseaseIdentification.utils.TRACK_LIFECYCLE_EVENTS
import com.zanoapp.applediseaseIdentification.utils.TRACK_SIGNUP_STATE

@Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private lateinit var signUpViewModel: SignUpViewModel
    private var currentUserAvailable: FirebaseUser? = null


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        initViewModel()
        Log.i(
            TRACK_SIGNUP_STATE,
            "onCreate: user status here is ${signUpViewModel.authenticationState.value}"
        )

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.i(TRACK_LIFECYCLE_EVENTS, "onCreateView: has been called ${Math.random()}")
        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_sign_up, container, false)
        binding.lifecycleOwner = this

        return binding.root
    }


    private fun userObserver() {

        Log.i(TRACK_LIFECYCLE_EVENTS, "userObserver: Has been called ${Math.random()}")
        signUpViewModel.user.observe(viewLifecycleOwner) { firebaseUser ->
            firebaseUser?.let { currentUser ->
                currentUserAvailable = currentUser
                Log.i(TRACK_SIGNUP_STATE, "#2 user data : ${currentUserAvailable!!.email}")
                authenticationObserver()
            }
            Log.i(TRACK_SIGNUP_STATE, "userObserverCalled")
        }
    }

    private fun authenticationObserver() {

        signUpViewModel.authenticationState.observe(viewLifecycleOwner) {
            currentUserAvailable = signUpViewModel.user.value
            Log.i(TRACK_SIGNUP_STATE, "authenticationObserver: method has been called ")
            when (it) {
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
            Log.i(TRACK_SIGNUP_STATE, "observer value is: ${it.name}")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TRACK_LIFECYCLE_EVENTS, "onViewCreated: has been called ${Math.random()}")

        binding.SignUpButton.setOnClickListener {
            signUpViewModel.signInWithGoogle(requireActivity())
            // progressBar.visibility = View.VISIBLE
        }
        signupWithEmail()
        authenticationObserver()
        userObserver()

    }

    private fun initViewModel() {
        val application = Application()
        val viewModelFactory = SignUpViewModelFactory(application)
        signUpViewModel = ViewModelProvider(this, viewModelFactory).get(SignUpViewModel::class.java)


    }

    private fun signupWithEmail() {
        binding.signUpWithEmailButton.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_userProfileDataFragment)
        }
    }
}


