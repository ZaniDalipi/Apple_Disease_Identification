package com.zanoapp.applediseaseIdentification

import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.zanoapp.applediseaseIdentification.ui.authenticationFirebase.SignUpViewModel
import com.zanoapp.applediseaseIdentification.ui.authenticationFirebase.SignUpViewModelFactory
import com.zanoapp.applediseaseIdentification.utils.REQ_ONE_TAP
import com.zanoapp.applediseaseIdentification.utils.TAG_ONE_TAP
import com.zanoapp.applediseaseIdentification.utils.TAG_VIEWMODEL
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var signUpViewModel: SignUpViewModel
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNav = findViewById(R.id.bottom_nav_view)
        fab = findViewById(R.id.floatingActionButton)

        val viewModelFactory = SignUpViewModelFactory(application)
        signUpViewModel = ViewModelProvider(this, viewModelFactory).get(SignUpViewModel::class.java)

        floatingActionButton.setOnClickListener {
            //findNavController(R.id.my_nav_host_fragment).navigate(R.id.action_mainActivity_to_cameraFragment)
            Snackbar.make(
                it,
                "Saved your data with location and the top 3 diseases that were classified in",
                Snackbar.LENGTH_LONG
            ).show()
        }

        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment

        appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds = setOf(
                R.id.userProfileDataFragment,
                R.id.cameraFragment,
                R.id.signUpFragment
            )
        )

        val navController = host.navController
        setupBottomNavBarWithNavigation(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.cameraFragment -> showBottomNav()
                R.id.userProfileDataFragment -> showBottomNav()
                R.id.locationFragment -> showBottomNav()
                else -> hideBottomNav()
            }
        }
    }

    private fun showBottomNav() {
        bottomNav.visibility = View.VISIBLE
        fab.visibility = View.VISIBLE
    }

    private fun hideBottomNav() {
        bottomNav.visibility = View.GONE
        fab.visibility = View.GONE
    }


    private fun setupBottomNavBarWithNavigation(navController: NavController) {
        bottomNav.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.my_nav_host_fragment).navigateUp(appBarConfiguration)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        signUpViewModel.onActivityResult(requestCode, resultCode, data, this)
    }

}
