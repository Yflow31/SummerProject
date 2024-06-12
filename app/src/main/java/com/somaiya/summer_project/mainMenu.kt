package com.somaiya.summer_project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
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

class MainMenu : AppCompatActivity() {

    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu2)
        val toolbar = findViewById<Toolbar>(R.id.login_toolbar)
        setSupportActionBar(toolbar)

        val user = Firebase.auth.currentUser
        if (user == null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
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
        userNameTextView.setText(user?.displayName.toString())
        userEmailTextView.setText(user?.email)

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
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