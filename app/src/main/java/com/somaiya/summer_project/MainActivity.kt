package com.somaiya.summer_project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.somaiya.summer_project.login.Repository.RepositoryLogin
import com.somaiya.summer_project.login.Repository.ViewModelLogin
import com.somaiya.summer_project.login.Repository.ViewModelFactoryLogin
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.somaiya.summer_project.timezone.retrofit.RetrofitClient
import com.somaiya.summer_project.timezone.retrofit.TimeApiService
import com.somaiya.summer_project.timezone.timezoneRepository
import com.somaiya.summer_project.timezone.timezoneViewModel
import com.somaiya.summer_project.timezone.timezoneViewModelFactory
import com.somaiya.summer_project.utils.UtiliMethods
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    private val loginRepository = RepositoryLogin()
    private val loginViewModel: ViewModelLogin by viewModels {
        ViewModelFactoryLogin(loginRepository)
    }
    private lateinit var editTextTextEmailAddress: EditText
    private lateinit var editTextTextPassword: EditText
    private lateinit var loginbtn: TextView
    private lateinit var signinbtn: TextView

    //TimeZone
    private val viewModel: timezoneViewModel by viewModels {
        timezoneViewModelFactory(timezoneRepository(RetrofitClient.retrofit.create(TimeApiService::class.java)), application)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel.startFetchingTimePeriodically("Asia", "Kolkata")

        //TextViews
        editTextTextEmailAddress = findViewById(R.id.editTextTextEmailAddress)
        editTextTextPassword = findViewById(R.id.editTextTextPassword)

        //Button
        loginbtn = findViewById(R.id.loginbtn)
        signinbtn = findViewById(R.id.signinbtn)

        val user = Firebase.auth.currentUser
        if (user != null) {
            val intent = Intent(this, MainMenu::class.java)
            startActivity(intent)
            finish()
        }


        loginbtn.setOnClickListener {

            lifecycleScope.launch {
                val email = editTextTextEmailAddress.text.toString()
                val password = editTextTextPassword.text.toString()
                if (email.isNotEmpty() || password.isNotEmpty()) {
                    loginViewModel.login(email, password)
                        .observe(this@MainActivity) { loginResponse ->
                            if (loginResponse != null) {
                                if (loginResponse.loading) {
                                    // Show loading indicator
                                    Toast.makeText(this@MainActivity, "Loading", Toast.LENGTH_SHORT).show()
                                } else if (loginResponse.success) {
                                    // Login successful
                                    val intent = Intent(this@MainActivity, MainMenu::class.java)
                                    startActivity(intent)
                                    viewModel.stopFetchingTime()
                                    finish()
                                }
                            }
                        }
                }
            }
        }

        signinbtn.setOnClickListener {
            val Intent = Intent(this@MainActivity, SignUp::class.java)
            startActivity(Intent)
            viewModel.stopFetchingTime()
            finish()
        }
    }
}