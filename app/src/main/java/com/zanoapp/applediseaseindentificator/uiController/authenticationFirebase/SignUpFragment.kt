package com.zanoapp.applediseaseindentificator.uiController.authenticationFirebase

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.*
import com.zanoapp.applediseaseindentificator.R
import com.zanoapp.applediseaseindentificator.databinding.SignUpFragmentBinding

class SignUpFragment : Fragment() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: SignUpFragmentBinding
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


    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(
            signInIntent,
            RC_SIGN_IN
        )
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

        if (requestCode == RC_SIGN_IN) {
            val completedTask = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                account = completedTask.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
                updateUI(null)
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        Log.d(TAG, "firebaseAuthWithGoogle: ${account?.id}")

        val credentials = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth.signInWithCredential(credentials)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        activity,
                        "Successfully signed in : ${account?.email}",
                        Toast.LENGTH_SHORT
                    ).show()
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Snackbar.make(requireView(), "Authentication Failed.", Snackbar.LENGTH_SHORT)
                        .show()
                    updateUI(null)
                }
            }
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

    companion object {
        private const val RC_SIGN_IN = 9001
        private const val TAG = "FirebaseUserControl"
    }
}
