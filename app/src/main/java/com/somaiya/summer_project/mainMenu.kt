package com.somaiya.summer_project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.somaiya.summer_project.signup.Model.DataSignUp
import com.somaiya.summer_project.timezone.retrofit.RetrofitClient
import com.somaiya.summer_project.timezone.retrofit.TimeApiService
import com.somaiya.summer_project.timezone.timezoneRepository
import com.somaiya.summer_project.timezone.timezoneViewModel
import com.somaiya.summer_project.timezone.timezoneViewModelFactory
import com.somaiya.summer_project.utils.UtiliMethods

class MainMenu : AppCompatActivity() {

    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    //TimeZone
    private val viewModel: timezoneViewModel by viewModels {
        timezoneViewModelFactory(timezoneRepository(RetrofitClient.retrofit.create(TimeApiService::class.java)), application)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu2)

        viewModel.startFetchingTimePeriodically("Asia", "Kolkata")

        val toolbar = findViewById<Toolbar>(R.id.login_toolbar)
        setSupportActionBar(toolbar)

        val user = Firebase.auth.currentUser
        if (user == null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }


        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationview)
        // Initialize navigation view and drawer layout
        navigationView.setupWithNavController(navController)

        // Initialize AppBarConfiguration with the drawer layout and the navigation graph
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        setupActionBarWithNavController(navController,appBarConfiguration)

        val headerView = navigationView.getHeaderView(0)
        val userNameTextView = headerView.findViewById<TextView>(R.id.user_name)
        val userEmailTextView = headerView.findViewById<TextView>(R.id.user_email)
        userNameTextView.setText(user?.displayName)
        Log.d("displayname", "onCreate: {$user.displayName.toString()}")
        userEmailTextView.setText(user?.email)
        Log.d("displayname", "onCreate: {$user.email")

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.homeFragment -> {
                    navController.navigate(R.id.homeFragment)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }

                R.id.historyFragment -> {
                    navController.navigate(R.id.historyFragment)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }

                R.id.profileFragment -> {
                    navController.navigate(R.id.profileFragment)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }

                R.id.logOutFragment -> {
                    val auth = Firebase.auth
                    auth.signOut()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                else -> false
            }
        }




    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}