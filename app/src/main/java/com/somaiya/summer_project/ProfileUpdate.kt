package com.somaiya.summer_project

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
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

        //Button
        updatebtn = findViewById(R.id.updatebtn)
        backbtn = findViewById(R.id.backbtn)

        updatebtn.setOnClickListener {
            lifecycleScope.launch {
                val firstname = firstnametxt.text.toString()
                val lastname = lastnametxt.text.toString()
                val coursename = coursenametxt.text.toString()
                val rollno = rollnotxt.text.toString()
                val email = "john.mckinley@examplepetstore.com"
                val div = "B"
                if (firstname.isNotEmpty() || lastname.isNotEmpty() || coursename.isNotEmpty() || rollno.isNotEmpty()) {
                    val profile = ProfileData(firstname, lastname ,email, coursename, div, rollno)
                    updateprf(profile)
                }
            }
        }
    }

//    val firstName: String,
//    val lastName: String,
//    val email: String,
//    val course: String,
//    val div: String,
//    val rollNo: String
    private suspend fun updateprf(profile: ProfileData) {
        ViewModelProfile.updateProfile(profile)
    }
}