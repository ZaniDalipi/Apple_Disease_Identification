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
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.*
import com.zanoapp.applediseaseindentificator.R
import com.zanoapp.applediseaseindentificator.databinding.SignUpFragmentBinding
import kotlin.math.sign

class SignUpFragment : Fragment() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: SignUpFragmentBinding
    private var account: GoogleSignInAccount? = null
    lateinit var signUpViewModel: SignUpViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.sign_up_fragment, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        val viewModelFactory = SignUpViewModelFactory(null)
        signUpViewModel = ViewModelProvider(this, viewModelFactory).get(SignUpViewModel::class.java)

        signUpViewModel.spinner.observe(this, Observer { spinner ->
            when (spinner) {
                true -> binding.progressBar.visibility = View.VISIBLE
                false -> binding.progressBar.visibility = View.GONE
            }
        })

        binding.signUpButton.setOnClickListener {
            signUpViewModel.signInWithGoogle(activity ?: throw Exception("no activity"))
            updateUI(signUpViewModel.user.value)
        }

        binding.signOutButton.setOnClickListener {
            signUpViewModel.userState.observe(this, Observer { userSate ->
                userSate?.let {
                    signUpViewModel.signOutUser()
                    auth.signOut()
                }
            })
            binding.signOutButton.visibility = View.GONE
        }

        signUpViewModel.toast.observe(this, Observer { message ->
            message?.let {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                signUpViewModel.onToastShown()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        val currentUser = signUpViewModel.user.value
        updateUI(currentUser)
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

}
