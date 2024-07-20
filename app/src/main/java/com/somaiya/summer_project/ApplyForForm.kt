package com.somaiya.summer_project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.somaiya.summer_project.applyform.Model.ApplyFormData
import com.somaiya.summer_project.applyform.Repository.RepositoryApplyForm
import com.somaiya.summer_project.applyform.Repository.ViewModelApplyForm
import com.somaiya.summer_project.applyform.Repository.ViewModelFactoryApplyForm
import com.somaiya.summer_project.R
import kotlinx.coroutines.launch

class ApplyForForm : AppCompatActivity() {

    lateinit var reason_for_being_late: EditText
    lateinit var location: EditText
    lateinit var times_late: EditText

    lateinit var submit_btn: Button
    lateinit var backtomainbtn: Button


    private val applyFormRepository = RepositoryApplyForm()
    private val ViewModelApplyForm: ViewModelApplyForm by viewModels {
        ViewModelFactoryApplyForm(applyFormRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_apply_for_form)

        //EditText
        reason_for_being_late = findViewById(R.id.reason_for_being_late)
        location = findViewById(R.id.location)
        times_late = findViewById(R.id.times_late)
        //Button
        submit_btn = findViewById(R.id.submitbtn)
        backtomainbtn = findViewById(R.id.backtomainbtn)

        val user = Firebase.auth.currentUser

        submit_btn.setOnClickListener {
            lifecycleScope.launch {
                val reason = reason_for_being_late.text.toString()
                val location = location.text.toString()
                val times_late = times_late.text.toString()
                val email = user?.email.toString()

                if (reason.isNotEmpty() || location.isNotEmpty() || times_late.isNotEmpty()) {
                    val form = ApplyFormData(reason, location, times_late,email)
                    submitform(form)
                }
            }
        }

        backtomainbtn.setOnClickListener {
            val intent = Intent(this, MainMenu::class.java)
            startActivity(intent)
        }



    }

    private suspend fun submitform(form: ApplyFormData) {
        ViewModelApplyForm.submitForm(form)
    }
}