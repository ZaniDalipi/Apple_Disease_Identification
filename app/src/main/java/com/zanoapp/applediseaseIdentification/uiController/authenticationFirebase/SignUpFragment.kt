package com.zanoapp.applediseaseIdentification.uiController.authenticationFirebase

import android.app.Application
import android.content.Intent
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.zanoapp.applediseaseIdentification.R
import com.zanoapp.applediseaseIdentification.databinding.SignUpFragmentBinding
import com.zanoapp.applediseaseIdentification.uiController.authenticationFirebase.SignUpViewModel.AuthenticationState.*
import com.zanoapp.applediseaseIdentification.uiController.authenticationFirebase.SignUpViewModel.Companion.TAG
import kotlinx.android.synthetic.main.sign_up_fragment.*

class SignUpFragment : Fragment() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: SignUpFragmentBinding
    private lateinit var signUpViewModel: SignUpViewModel
    private var currentUserAvailable: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        initGoogleSignInClient()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.sign_up_fragment, container, false)
        binding.lifecycleOwner = this

        binding.signUpButton.setOnClickListener {
            signUpViewModel.signInWithGoogle(requireActivity())
            progress_bar.visibility = View.VISIBLE
        }
        return binding.root
    }


    private fun userObserver() {
        signUpViewModel.user.observe(this, Observer { firebaseUser ->
            firebaseUser?.let { currentUser ->
                currentUserAvailable = currentUser
                Log.i(TAG, "#2 user data : ${currentUserAvailable!!.email}")
                authenticationObserver()
            }
            Log.i(TAG, "userObserverCalled")
        })
    }
    private fun authenticationObserver() {
        signUpViewModel.authenticationState.observe(this, Observer {authState ->
            currentUserAvailable = signUpViewModel.user.value
            Log.i(TAG, "authObserverCalled")
            when (authState) {
                AUTHENTICATED -> {
                    if (findNavController().currentDestination?.id == R.id.signUpFragment) {
                        findNavController().navigate(R.id.action_signUpFragment_to_userProfileDataFragment)
                    }
                    Toast.makeText(activity,"Welcome you have been recognized as ${currentUserAvailable?.email},Enjoy the application",Toast.LENGTH_SHORT).show()
                }
                UNAUTHENTICATED -> {
                    Toast.makeText(activity, "you are not authenticated, please authenticate to enjoy the app", Toast.LENGTH_SHORT).show()

                }
                INVALID_AUTH -> {
                    Toast.makeText(activity, "something went wrong", Toast.LENGTH_SHORT).show()
                }
            }
            Log.i(TAG, "observer value is: ${authState.name}")
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userObserver()
        authenticationObserver()
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        signUpViewModel.onActivityResult(requestCode, resultCode, data, requireActivity())


    }

    private fun initGoogleSignInClient() {
        auth = FirebaseAuth.getInstance()
        val googleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("939177967297-qtssncnkb41vqps404s5tq7onceocrph.apps.googleusercontent.com")
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
