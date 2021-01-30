package com.zanoapp.applediseaseIdentification.ui.userProfileData


import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

import com.zanoapp.applediseaseIdentification.R
import com.zanoapp.applediseaseIdentification.databinding.UserProfileDataFragmentBinding
import com.zanoapp.applediseaseIdentification.ui.authenticationFirebase.SignUpViewModel
import com.zanoapp.applediseaseIdentification.ui.authenticationFirebase.SignUpViewModelFactory

class UserProfileDataFragment : Fragment() {

    private lateinit var binding: UserProfileDataFragmentBinding
    private lateinit var signUpViewModel: SignUpViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.user_profile_data_fragment,
            container,
            false
        )
        binding.signUpViewModel = signUpViewModel
        binding.lifecycleOwner = this

        binding.signOutButton.setOnClickListener {
            signOutUser()
        }

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        initGoogleSignInOptions()
    }

    private fun initViewModel() {
        val application = Application()
        val viewModelFactory = SignUpViewModelFactory(application)
        signUpViewModel = ViewModelProvider(this, viewModelFactory).get(SignUpViewModel::class.java)
    }
    private fun initGoogleSignInOptions(){
        auth = FirebaseAuth.getInstance()
        val googleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("939177967297-qtssncnkb41vqps404s5tq7onceocrph.apps.googleusercontent.com")
                .requestEmail()
                .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), googleSignInOptions)
    }

    /** Sign Out user*/
    private fun signOutUser(){

        auth.signOut()
        googleSignInClient.signOut().addOnCompleteListener {
            Toast.makeText(activity,"you have logged out : ${signUpViewModel.user.value?.displayName} ",Toast.LENGTH_SHORT).show()
            signUpViewModel._authenticationState.value = SignUpViewModel.AuthenticationState.UNAUTHENTICATED
            findNavController().navigate(R.id.action_userProfileDataFragment_to_signUpFragment)
        }
    }
}
