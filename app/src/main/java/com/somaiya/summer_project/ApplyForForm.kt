package com.somaiya.summer_project

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.Timestamp
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
    lateinit var subject_name: EditText
    lateinit var faculty_name: EditText
    lateinit var radioGroup: RadioGroup

    lateinit var submit_btn: TextView
    lateinit var backtomainbtn: ImageButton
    private lateinit var Cal: Calendar
    private lateinit var firestore: FirebaseFirestore
    lateinit var radio1: RadioButton
    lateinit var radio2: RadioButton
    lateinit var radio3: RadioButton
    lateinit var radio4: RadioButton


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
        faculty_name = findViewById(R.id.faculty_name)
        subject_name = findViewById(R.id.subject_name)
        radioGroup = findViewById(R.id.radioGroup)

        //Radio buttons
        radio1 = findViewById<RadioButton>(R.id.radio1)
        radio2 = findViewById<RadioButton>(R.id.radio2)
        radio3 = findViewById<RadioButton>(R.id.radio3)
        radio4 = findViewById<RadioButton>(R.id.radio4)

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
                        submit_btn.setBackgroundColor(
                            ContextCompat.getColor(
                                this@ApplyForForm,
                                R.color.grey
                            )
                        )
                    } else {
                        submit_btn.isEnabled = true
                    }
                }
            }

        var selectedOption: String = ""


        radio1.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                selectedOption = "09:00pm-10:20pm"
                resetRadioButtons(1)
            }
        }

        radio2.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                selectedOption = "10:30pm-11:50pm"
                resetRadioButtons(2)
            }
        }

        radio3.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                selectedOption = "01:00pm-02:20pm"
                resetRadioButtons(3)
            }
        }

        radio4.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                selectedOption = "02:30pm-03:20pm"
                resetRadioButtons(4)
            }
        }


        submit_btn.setOnClickListener {

            lifecycleScope.launch {
                val reason = reason_for_being_late.text.toString()
                val location = location.text.toString()
                val userid = user?.uid ?: ""
                val email = user?.email ?: ""
                val subject = subject_name.text.toString()
                val faculty = faculty_name.text.toString()
                val times_late = times_late.text.toString()
                val selectedTimeSlot = selectedOption

                if (reason.isNotEmpty() || location.isNotEmpty() || radioGroup.checkedRadioButtonId != -1) {
                    val form = ApplyFormData(
                        reason,
                        location,
                        times_late,
                        email,
                        userid,
                        reasonId = "",
                        approvalStatus = "",
                        role = "",
                        currentdate = formatteddate,
                        currenttime = formattedtime,
                        subject = subject,
                        faculty = faculty,
                        selectedTimeSlot = selectedTimeSlot
                    )
                    submitform(form)
                    submit_btn.isEnabled = false
                    submit_btn.setBackgroundColor(
                        ContextCompat.getColor(
                            this@ApplyForForm,
                            R.color.grey
                        )
                    )
                }
            }
        }

        backtomainbtn.setOnClickListener {
            val intent = Intent(this, MainMenu::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun resetRadioButtons(index: Int) {
        when (index) {
            1 -> {
                radio1.setChecked(true)
                radio2.setChecked(false)
                radio3.setChecked(false)
                radio4.setChecked(false)
            }

            2 -> {
                radio1.setChecked(false)
                radio2.setChecked(true)
                radio3.setChecked(false)
                radio4.setChecked(false)
            }

            3 -> {
                radio1.setChecked(false)
                radio2.setChecked(false)
                radio3.setChecked(true)
                radio4.setChecked(false)
            }

            4 -> {
                radio1.setChecked(false)
                radio2.setChecked(false)
                radio3.setChecked(false)
                radio4.setChecked(true)

            }
        }
    }

    private suspend fun submitform(form: ApplyFormData) {
        ViewModelApplyForm.submitForm(form)
    }
}