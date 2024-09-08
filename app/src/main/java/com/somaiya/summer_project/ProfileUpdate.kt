package com.somaiya.summer_project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.somaiya.summer_project.pofileupdate.Model.ProfileData
import com.somaiya.summer_project.pofileupdate.Repository.RepositoryProfile
import com.somaiya.summer_project.pofileupdate.Repository.ViewModelFactoryProfile
import com.somaiya.summer_project.pofileupdate.Repository.ViewModelProfile
import com.somaiya.summer_project.utils.Loader
import kotlinx.coroutines.launch

class ProfileUpdate : AppCompatActivity() {

    //Loaders


    lateinit var firstnametxt: EditText
    lateinit var lastnametxt: EditText
    lateinit var coursenametxt: EditText
    lateinit var rollnotxt: EditText
    lateinit var role_text: TextView

    lateinit var updatebtn: TextView
    lateinit var backbtn: ImageButton
    lateinit var phonenotxt: EditText
    lateinit var divtxt: EditText
    lateinit var db: FirebaseFirestore
    private val user = Firebase.auth.currentUser

    var role: String = "Unknown"


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
        phonenotxt = findViewById(R.id.phonenotxt)
        divtxt = findViewById(R.id.divtxt)

        // TextView


        //Button
        updatebtn = findViewById(R.id.updatebtn)
        backbtn = findViewById(R.id.backbtn)

        //Firebase
        db = FirebaseFirestore.getInstance()

        if (user != null) {
            db.collection("USERS").document(user.uid).get().addOnSuccessListener { documentSnapshot ->
                role = documentSnapshot.getString("role") ?: "Unknown" // Default to "Unknown" if role is null
                firstnametxt.setText(documentSnapshot.getString("firstName")?: "Unknown")
                lastnametxt.setText(documentSnapshot.getString("lastName")?: "Unknown")
                coursenametxt.setText(documentSnapshot.getString("course")?: "Unknown")
                rollnotxt.setText(documentSnapshot.getString("rollNo")?: "Unknown")
                phonenotxt.setText(documentSnapshot.getString("phoneNumber")?: "Unknown")
                divtxt.setText(documentSnapshot.getString("div")?: "Unknown")
            }.addOnFailureListener {
                // Handle any failures here, maybe set a default role
                role = "Unknown"
                firstnametxt.setText("Unknown")
                lastnametxt.setText("Unknown")
                coursenametxt.setText("Unknown")
                rollnotxt.setText("Unknown")
                phonenotxt.setText("Unknown")
                divtxt.setText("Unknown")
            }
        }


        updatebtn.setOnClickListener {

            lifecycleScope.launch {
                val firstname = firstnametxt.text.toString()
                val lastname = lastnametxt.text.toString()
                val course = coursenametxt.text.toString()
                val rollNo = rollnotxt.text.toString()
                val displayname = firstnametxt.text.toString()
                val phonenumber = phonenotxt.text.toString()
                val div = divtxt.text.toString()

                if (firstname.isNotEmpty() || lastname.isNotEmpty() || course.isNotEmpty() || rollNo.isNotEmpty()) {
                    val profile = ProfileData(
                        firstname,
                        lastname,
                        displayname,
                        phonenumber,
                        course,
                        div,
                        role,
                        rollNo
                    )
                    updateprf(profile)
                }
            }
        }

        backbtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private suspend fun updateprf(profile: ProfileData) {
        ViewModelProfile.updateProfile(profile)
    }

}