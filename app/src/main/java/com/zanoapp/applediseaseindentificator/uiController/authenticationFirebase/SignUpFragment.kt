package com.zanoapp.applediseaseindentificator.uiController.authenticationFirebase

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.*

import com.zanoapp.applediseaseindentificator.R
import com.zanoapp.applediseaseindentificator.databinding.SignUpFragmentBinding
import com.zanoapp.applediseaseindentificator.uiController.authenticationFirebase.SignUpViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val RC_SIGN_IN = 9001
private const val TAG = "FirebaseUserControl"

class SignUpFragment : Fragment() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: SignUpFragmentBinding
    private lateinit var viewModel: SignUpViewModel
    private val uiScope = CoroutineScope(Dispatchers.Main)
    private var account: GoogleSignInAccount? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.sign_up_fragment, container, false)
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signUpButton.setOnClickListener {
            signIn()
        }
        binding.signOutButton.setOnClickListener {
            signOutCurrentUser()
            binding.signOutButton.visibility = View.GONE
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        auth = FirebaseAuth.getInstance()


    }

    fun signOutCurrentUser() {

        val currentUser = account
        currentUser?.isExpired
        Toast.makeText(activity, "you have logged out : ${currentUser?.displayName}", Toast.LENGTH_SHORT).show()
        binding.signOutButton.visibility = View.GONE
        binding.signUpButton.visibility = View.VISIBLE
        binding.userInfoTextView.text = "You have signed out Please signin with google "
    }


    override fun onStart() {
        super.onStart()


        if (account != null) {
            binding.signUpButton.visibility = Button.GONE
            binding.signOutButton.visibility = View.VISIBLE
            } else {
            binding.signUpButton.visibility = Button.VISIBLE
            binding.signOutButton.visibility = View.GONE
        }

        if (binding.userInfoTextView != null) {
            binding.userInfoTextView.text =
                "current user logged in is ${account?.email}"
            Toast.makeText(context, "Welcome ${account?.email}", Toast.LENGTH_LONG).show()
        } else {
            binding.userInfoTextView.text = getString(R.string.no_user_signed_in)
        }

    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(
            signInIntent,
            RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            account = completedTask.getResult(ApiException::class.java)
            account?.let {
                Log.d(TAG, "firebaseAuthWithGoogle ${account!!.id}")
                val credentials = GoogleAuthProvider.getCredential(account!!.idToken, null)
                auth.signInWithCredential(credentials).addOnCompleteListener {
                    if (completedTask.isSuccessful) {
                        Toast.makeText(
                                activity,
                                "Succesfully signed in ${account!!.email}",
                                Toast.LENGTH_SHORT
                            )
                            .show()
                        account?.let { updateUI(account) }
                        //findNavController().navigate(R.id.action_signUpFragment_to_cameraFragment)
                    }
                }
            }
        } catch (e: ApiException) {
            Toast.makeText(activity, "Signing not succesfull", Toast.LENGTH_SHORT).show()
            Snackbar.make(view!!, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
            updateUI(null)
        }
    }


    @SuppressLint("SetTextI18n")
    fun updateUI(account: GoogleSignInAccount?) {

        binding.signOutButton.visibility = View.VISIBLE
        if (account != null) {
            binding.signUpButton.visibility = View.GONE
            binding.userInfoTextView.text = "Current User logged in : ${account.email.toString()}"
            Toast.makeText(
                activity,
                "Welcome ${account?.email.toString()}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}
