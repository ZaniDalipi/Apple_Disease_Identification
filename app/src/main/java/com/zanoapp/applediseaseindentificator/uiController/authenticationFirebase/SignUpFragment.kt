package com.zanoapp.applediseaseindentificator.uiController.authenticationFirebase

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.*
import com.zanoapp.applediseaseindentificator.R
import com.zanoapp.applediseaseindentificator.databinding.SignUpFragmentBinding
import com.zanoapp.applediseaseindentificator.uiController.authenticationFirebase.SignUpViewModel.Companion.RC_SIGN_IN
import kotlin.math.sign

class SignUpFragment : Fragment() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: SignUpFragmentBinding
    private var account: GoogleSignInAccount? = null
    lateinit var signUpViewModel: SignUpViewModel
    lateinit var credentials: AuthCredential

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.sign_up_fragment, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initGoogleSignInClient()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initViewModel()
        initSignInButton()
        initSignOutButton()

        signUpViewModel.toast.observe(this, Observer { message ->
            message?.let {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                signUpViewModel.onToastShown()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        signUpViewModel.user.observe(this, Observer { firebaseUser ->
            firebaseUser?.let { currentUser ->
                Toast.makeText(
                        requireContext(),
                        "welcome back : ${currentUser.email}",
                        Toast.LENGTH_SHORT
                    )
                    .show()
                updateUI(currentUser)
            }
        })
    }

    private fun signOutCurrentUser() {

        auth.signOut()
        googleSignInClient.signOut().addOnCompleteListener {
            Toast.makeText(
                activity,
                "you have logged out : ${account?.displayName} ",
                Toast.LENGTH_SHORT
            ).show()
            binding.signOutButton.visibility = View.GONE
            binding.signUpButton.visibility = View.VISIBLE
            binding.userInfoTextView.text =
                getString(R.string.after_sign_out_msg)
            updateUI(null)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        signUpViewModel.onActivityResult(requestCode, resultCode, data, Activity())
    }


    @SuppressLint("SetTextI18n")
    private fun updateUI(user: FirebaseUser?) {

        if (user != null) {
            binding.userInfoTextView.text = """current user signed in is : ${user.email}"""

            binding.signUpButton.visibility = View.GONE
            binding.signOutButton.visibility = View.VISIBLE
        } else {
            binding.userInfoTextView.text = getString(R.string.no_user_signed_in)
            binding.signUpButton.visibility = View.VISIBLE
            binding.signOutButton.visibility = View.GONE
        }
    }


    private fun initSignInButton() {
        binding.signUpButton.setOnClickListener {
            signUpViewModel.signInWithGoogle(requireActivity())
        }
    }

    private fun initSignOutButton() {
        binding.signOutButton.setOnClickListener {
            signOutCurrentUser()
            binding.signOutButton.visibility = View.GONE
        }
    }

    private fun initGoogleSignInClient() {
        auth = FirebaseAuth.getInstance()
        val googleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(R.string.default_web_client_id.toString())
                .requestEmail()
                .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), googleSignInOptions)
    }

    private fun initViewModel() {
        val viewModelFactory = SignUpViewModelFactory(null)
        signUpViewModel = ViewModelProvider(this, viewModelFactory).get(SignUpViewModel::class.java)

    }


}
