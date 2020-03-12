package com.zanoapp.applediseaseindentificator.uiController

import android.content.Intent
import android.opengl.Visibility
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.*
import com.zanoapp.applediseaseindentificator.MainActivity

import com.zanoapp.applediseaseindentificator.R
import com.zanoapp.applediseaseindentificator.databinding.SignUpFragmentBinding
import kotlinx.android.synthetic.main.sign_up_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

private const val RC_SIGN_IN = 1

class SignUpFragment : Fragment() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: SignUpFragmentBinding
    private lateinit var viewModel: SignUpViewModel
    private val uiScope = CoroutineScope(Dispatchers.Main)

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


    override fun onStart() {
        super.onStart()

        val currentUser = GoogleSignIn.getLastSignedInAccount(context)
        if (binding.userInfoTextView != null) {
            binding.userInfoTextView.text =
                "current user logged in is $currentUser"
            Toast.makeText(context, "Welcome ${currentUser?.email}", Toast.LENGTH_LONG).show()
        } else {
            binding.userInfoTextView.text = getString(R.string.no_user_signed_in)
        }

        if (currentUser != null) {
            binding.signUpButton.visibility = Button.GONE
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        uiScope.launch {
            try {
                val account = completedTask.getResult(ApiException::class.java)
                account?.let {
                    val credentials = GoogleAuthProvider.getCredential(account?.idToken, null)
                    auth.signInWithCredential(credentials).addOnCompleteListener {
                        if (completedTask.isSuccessful) {
                            Toast.makeText(
                                    activity,
                                    "Succesfully signed in ${account?.email}",
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                            findNavController().navigate(R.id.action_signUpFragment_to_cameraFragment)
                            val user: FirebaseUser? = auth.currentUser
                            user?.let { it1 -> updateUI(it1) }
                        }
                    }
                }
            } catch (e: ApiException) {
                Toast.makeText(activity, "Signing not succesfull", Toast.LENGTH_SHORT).show()
                Log.w("waringing", "Signin resut failed" + e.statusCode)
            }
        }
    }

   
    fun updateUI(firebaseUser: FirebaseUser) {

        binding.signOutButton.visibility = View.VISIBLE
        val account = GoogleSignIn.getLastSignedInAccount(activity)
        if (account != null) {
            binding.userInfoTextView.text = account.email.toString()
            Toast.makeText(
                activity,
                "Welcome ${account.email}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}
