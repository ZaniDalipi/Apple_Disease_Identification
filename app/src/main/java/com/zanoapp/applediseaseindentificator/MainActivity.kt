package com.zanoapp.applediseaseindentificator

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.zanoapp.applediseaseindentificator.uiController.authenticationFirebase.SignUpViewModel
import com.zanoapp.applediseaseindentificator.uiController.authenticationFirebase.SignUpViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var signUpViewModel: SignUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewModelFactory = SignUpViewModelFactory(null)
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
                R.id.dataFragment,
                R.id.cameraFragment
            )
        )

        val navController = host.navController
        setupBottomNavBarWithNaviation(navController)
        //   setupActionBar(navController, appBarConfiguration)

        /*      navController.addOnDestinationChangedListener {_ , destination, _ ->
                  if (destination.id == R.id.cameraFragment){
                      toolbar.hideOverflowMenu()
                  }
              }*/

    }

    private fun setupBottomNavBarWithNaviation(navController: NavController) {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        bottomNavigation.setupWithNavController(navController)
    }

    /* fun setupActionBar(navController: NavController, appBarConfiguration: AppBarConfiguration){
         setupActionBarWithNavController(navController, appBarConfiguration)
     }*/

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.my_nav_host_fragment).navigateUp(appBarConfiguration)
    }
}
