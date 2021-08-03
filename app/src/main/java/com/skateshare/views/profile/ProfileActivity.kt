package com.skateshare.views.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.skateshare.R
import com.skateshare.db.LocalRoutesDao
import com.skateshare.services.SHOW_RECORD_FRAGMENT
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

// TODO: Make ProfileActivity into a "MainActivity" class for all app fragments after authentication
class ProfileActivity : AppCompatActivity() {

    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_SkateShare)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.main_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavBar = findViewById<BottomNavigationView>(R.id.bottom_navigation_bar)
        bottomNavBar.setupWithNavController(navController)

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.feedFragment, R.id.routesFragment, R.id.recordFragment, R.id.profileFragment)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.feedFragment, R.id.routesFragment, R.id.recordFragment, R.id.profileFragment -> {
                    bottomNavBar.visibility = View.VISIBLE
                }
                else -> bottomNavBar.visibility = View.GONE
            }
        }

        navigateToRecordFragmentIfNeeded(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
    }

    private fun navigateToRecordFragmentIfNeeded(intent: Intent?) {
        if (intent?.action == SHOW_RECORD_FRAGMENT) {
            Log.i("ProfileActivity", "Navigate to record called!")
            navHostFragment.findNavController().navigate(R.id.action_global_record_fragment)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToRecordFragmentIfNeeded(intent)
    }
}