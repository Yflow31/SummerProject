package com.somaiya.summer_project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.somaiya.summer_project.applyform.Model.ApplyFormData
import com.somaiya.summer_project.applyform.Repository.RepositoryApplyForm
import com.somaiya.summer_project.applyform.Repository.ViewModelApplyForm
import com.somaiya.summer_project.applyform.Repository.ViewModelFactoryApplyForm
import com.somaiya.summer_project.R
import kotlinx.coroutines.launch

class ApplyForForm : AppCompatActivity() {

    lateinit var reason_for_being_late: EditText
    lateinit var location: EditText
    lateinit var times_late: TextView

    lateinit var submit_btn: Button
    lateinit var backtomainbtn: Button

    private lateinit var firestore: FirebaseFirestore


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

        firestore = FirebaseFirestore.getInstance()

        val user = Firebase.auth.currentUser

        if (user == null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }


        firestore.collection("USERS").document(user?.uid ?: "")
            .collection("reasons")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val count = task.result.size()
                    times_late.text = count.toString()
                    Log.d("times_late", "onCreate:{$times_late} ")
                } else {
                    Log.w("FirestoreCount", "Error getting documents.", task.exception)
                }
            }


        submit_btn.setOnClickListener {
            lifecycleScope.launch {
                val reason = reason_for_being_late.text.toString()
                val location = location.text.toString()
                val userid = user?.uid ?: ""
                val email = user?.email ?: ""


                if (reason.isNotEmpty() || location.isNotEmpty()) {
                    val form = ApplyFormData(reason,location,times_late.text.toString(),email,userid,reasonId = "",approvalStatus = "")
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