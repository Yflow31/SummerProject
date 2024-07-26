package com.somaiya.summer_project

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.somaiya.summer_project.pofileupdate.Model.ProfileData
import com.somaiya.summer_project.pofileupdate.Repository.RepositoryProfile
import com.somaiya.summer_project.pofileupdate.Repository.ViewModelFactoryProfile
import com.somaiya.summer_project.pofileupdate.Repository.ViewModelProfile
import kotlinx.coroutines.launch

class ProfileUpdate : AppCompatActivity() {

    lateinit var firstnametxt: EditText
    lateinit var lastnametxt: EditText
    lateinit var coursenametxt: EditText
    lateinit var rollnotxt: EditText

    lateinit var updatebtn: Button
    lateinit var backbtn: Button
    lateinit var displaynametxt: EditText
    lateinit var emailtxt: EditText
    lateinit var phonenotxt: EditText
    lateinit var divtxt: EditText
    lateinit var auth: FirebaseAuth


    private val profileRepository = RepositoryProfile()
    private val ViewModelProfile: ViewModelProfile by viewModels {
        ViewModelFactoryProfile(profileRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_update)

        //EditText
        firstnametxt = findViewById(R.id.firstnametxt)
        lastnametxt = findViewById(R.id.lastnametxt)
        coursenametxt = findViewById(R.id.coursenametxt)
        rollnotxt = findViewById(R.id.rollnotxt)
        emailtxt = findViewById(R.id.emailtxt)
        phonenotxt = findViewById(R.id.phonenotxt)
        divtxt = findViewById(R.id.divtxt)

        //Button
        updatebtn = findViewById(R.id.updatebtn)
        backbtn = findViewById(R.id.backbtn)
        displaynametxt = findViewById(R.id.displaynametxt)

        //Firebase
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        updatebtn.setOnClickListener {
            lifecycleScope.launch {
                val firstname = firstnametxt.text.toString()
                val lastname = lastnametxt.text.toString()
                val course = coursenametxt.text.toString()
                val rollNo = rollnotxt.text.toString()
                val displayname = displaynametxt.text.toString()
                val email = currentUser?.email ?: ""
                val phonenumber = phonenotxt.text.toString()
                val div = divtxt.text.toString()
                val role = "student"
                if (firstname.isNotEmpty() || lastname.isNotEmpty() || course.isNotEmpty() || rollNo.isNotEmpty()) {
                    val profile = ProfileData(firstname, lastname,displayname,phonenumber,email,course, div,role,rollNo)
                    updateprf(profile)
                }
            }
        }
    }

    private suspend fun updateprf(profile: ProfileData) {
        ViewModelProfile.updateProfile(profile)
    }
}