package com.somaiya.summer_project

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.somaiya.summer_project.pofileupdate.Model.ProfileData
import com.somaiya.summer_project.pofileupdate.Repository.RepositoryProfile
import com.somaiya.summer_project.pofileupdate.Repository.ViewModelFactoryProfile
import com.somaiya.summer_project.pofileupdate.Repository.ViewModelProfile
import com.somaiya.summer_project.utils.Loader
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.launch

class ProfileUpdate : AppCompatActivity() {

    //Loaders
    private var LOADER_SHOWING = false
    private var loadingInsuranceDialogueFragment: Loader? = null


    lateinit var firstnametxt: EditText
    lateinit var lastnametxt: EditText
    lateinit var coursenametxt: EditText
    lateinit var rollnotxt: EditText

    lateinit var main:LinearLayout

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
        main = findViewById(R.id.main)

        showLoadingMain(main)


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

                hideLoadingMain(main)

            }.addOnFailureListener {
                // Handle any failures here, maybe set a default role
                role = "Unknown"
                firstnametxt.setText("Unknown")
                lastnametxt.setText("Unknown")
                coursenametxt.setText("Unknown")
                rollnotxt.setText("Unknown")
                phonenotxt.setText("Unknown")
                divtxt.setText("Unknown")
                hideLoadingMain(main)
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

    private fun showLoadingMain(main: LinearLayout? = null) {
        if (!LOADER_SHOWING) {
            LOADER_SHOWING = true

            val fragmentManager = supportFragmentManager
            val fragment = fragmentManager.findFragmentByTag("loadingDialog") as Loader?

            if (fragment == null) {
                // No existing fragment found, create and show a new instance
                loadingInsuranceDialogueFragment = Loader.newInstance("Loading, Please wait...")
                loadingInsuranceDialogueFragment?.isCancelable = false
                loadingInsuranceDialogueFragment?.show(fragmentManager, "loadingDialog")
                main?.visibility = View.GONE
            } else if (!fragment.isAdded) {
                // If the fragment exists but hasn't been added, show it again.
                // This scenario is rare due to the lifecycle of DialogFragment.
                fragment.show(fragmentManager, "loadingDialog")
                main?.visibility = View.GONE
            }
            // If the fragment is already added, it should be visible and nothing needs to be done.
        }
    }

    private fun hideLoadingMain(main: LinearLayout? = null) {
        try {
            LOADER_SHOWING = false

            val fragmentManager = supportFragmentManager
            val fragment = fragmentManager.findFragmentByTag("loadingDialog") as Loader?
            if (fragment != null && fragment.isAdded) {
                fragment.dismiss()
                main?.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}