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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.zanoapp.applediseaseIdentification.R
import com.zanoapp.applediseaseIdentification.databinding.FragmentSignUpBinding
import com.zanoapp.applediseaseIdentification.ui.authenticationFirebase.SignUpViewModel.AuthenticationState.*
import com.zanoapp.applediseaseIdentification.utils.*
class SignUpFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
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

  /*  override fun onStart() {
        super.onStart()
        userObserver()
        Log.i(TAG_VIEWMODEL, "onStart: User state is : ${signUpViewModel.user.value}")
        authenticationObserver()
        Log.i(
            TAG_VIEWMODEL,
            "onStart: Authentication state : ${signUpViewModel.authenticationState.value} "
        )
    }*/

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
        signUpViewModel.user.observe(viewLifecycleOwner, Observer { firebaseUser ->
            firebaseUser?.let { currentUser ->
                currentUserAvailable = currentUser
                Log.i(TRACK_SIGNUP_STATE, "#2 user data : ${currentUserAvailable!!.email}")
                authenticationObserver()
            }
            Log.i(TRACK_SIGNUP_STATE, "userObserverCalled")
        })
    }

    private fun authenticationObserver() {

        signUpViewModel.authenticationState.observe(viewLifecycleOwner, Observer {
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
        })
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

    fun signupWithEmail() {
        binding.signUpWithEmailButton.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_userProfileDataFragment)
        }
    }


    /* private fun loginUser() {
         binding.signUpWithEmailButton.setOnClickListener {
             if (signUpViewModel.firebaseAuth.currentUser == null) {
                 val email = binding.email.editText?.text.toString()
                 val password = binding.passwordEditText.editText?.text.toString()

                 if (email.isNotEmpty() && password.isNotEmpty()) {
                     if (email != signUpViewModel.userObjectFromDb.value?.email) {
                         signUpViewModel.loginWithFirebase(email, password)
                         Toast.makeText(
                             context,
                             "Succesfully login in , Welcome ${signUpViewModel.user.value?.email}",
                             Toast.LENGTH_SHORT
                         ).show()
                         if (findNavController().currentDestination?.id == R.id.signUpFragment) {
                             findNavController().navigate(R.id.action_signUpFragment_to_userProfileDataFragment)
                         }


                     }


                 } else {
                     Toast.makeText(
                         context,
                         "You re already signed in by the next email : ${auth.currentUser!!.email}",
                         Toast.LENGTH_SHORT
                     ).show()
                 }
             }
         }
     }
 */
    /* private fun registerNewUser() {
         binding.SignUpButton.setOnClickListener {
             val email = binding.email.editText?.text.toString()
             val password = binding.passwordEditText.editText?.text.toString()
             val confirmPassword = binding.confirmPasswordEditText.editText?.text.toString()

             it.let {
                 if (email.isNotEmpty() && password.isNotEmpty()) {
                     if (password == confirmPassword) {
                         signUpViewModel.registerWithFirebase(email, password)
                         if (findNavController().currentDestination?.id == R.id.signUpFragment) {
                             findNavController().navigate(R.id.action_signUpFragment_to_userProfileDataFragment)
                         }
                     }
                 } else {
                     Toast.makeText(
                         context,
                         "Email Address and Password Must Be Entered",
                         Toast.LENGTH_SHORT
                     ).show()
                 }
             }

         }
     }

 }*/
}


