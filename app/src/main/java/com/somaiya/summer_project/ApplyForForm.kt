package com.somaiya.summer_project

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.somaiya.summer_project.applyform.Model.ApplyFormData
import com.somaiya.summer_project.applyform.Repository.RepositoryApplyForm
import com.somaiya.summer_project.applyform.Repository.ViewModelApplyForm
import com.somaiya.summer_project.applyform.Repository.ViewModelFactoryApplyForm
import com.somaiya.summer_project.R
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Timer

class ApplyForForm : AppCompatActivity() {

    lateinit var reason_for_being_late: EditText
    lateinit var location: EditText
    lateinit var times_late: TextView
    lateinit var dtimerc: TextView
    lateinit var dtimerc1: TextView

    lateinit var submit_btn: Button
    lateinit var backtomainbtn: Button
    private lateinit var Cal: Calendar
    private lateinit var firestore: FirebaseFirestore


    private val applyFormRepository = RepositoryApplyForm()
    private val ViewModelApplyForm: ViewModelApplyForm by viewModels {
        ViewModelFactoryApplyForm(applyFormRepository)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_apply_for_form)

        //EditText
        reason_for_being_late = findViewById(R.id.reason_for_being_late)
        location = findViewById(R.id.location)
        times_late = findViewById(R.id.times_late)
        dtimerc = findViewById(R.id.dtimerc)
        dtimerc1 = findViewById(R.id.dtimerc1)

        //Date
        val current = LocalDateTime.now()
        val formatterdate = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val formattertime = DateTimeFormatter.ofPattern("HH:mm")
        val formatteddate = current.format(formatterdate)
        val formattedtime = current.format(formattertime)

        Log.d("formatteddate", "onCreate: {$formattedtime}")
        //Button
        submit_btn = findViewById(R.id.submitbtn)
        backtomainbtn = findViewById(R.id.backtomainbtn)

        firestore = FirebaseFirestore.getInstance()
        Cal = Calendar.getInstance()

        val user = Firebase.auth.currentUser

        if (user == null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        //Displaying date and time

        dtimerc.text = formatteddate
        dtimerc1.text = formattedtime

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

        firestore.collection("USERS").document(user?.uid ?: "").get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val canCreateNewReason = documentSnapshot.getBoolean("canCreateNewReason")
                    if (canCreateNewReason == true) {
                        submit_btn.isEnabled = false
                    }
                }
            }


        submit_btn.setOnClickListener {

            lifecycleScope.launch {
                val reason = reason_for_being_late.text.toString()
                val location = location.text.toString()
                val userid = user?.uid ?: ""
                val email = user?.email ?: ""

                if (reason.isNotEmpty() || location.isNotEmpty()) {
                    val form = ApplyFormData(
                        reason,
                        location,
                        times_late.text.toString(),
                        email,
                        userid,
                        reasonId = "",
                        approvalStatus = "",
                        role = "",
                        currentdate = formatteddate,
                        currenttime = formattedtime
                    )
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