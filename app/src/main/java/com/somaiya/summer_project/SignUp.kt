package com.somaiya.summer_project

import RepositorySignUp
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.somaiya.summer_project.signup.Model.DataSignUp
import com.somaiya.summer_project.signup.Repository.ViewModelFactorySignUp
import com.somaiya.summer_project.signup.Repository.ViewModelSignUp
import kotlinx.coroutines.launch

class SignUp : AppCompatActivity() {

    private val repositorySignUp = RepositorySignUp()
    private val viewModelSignUp: ViewModelSignUp by viewModels {
        ViewModelFactorySignUp(repositorySignUp)
    }

    private lateinit var editTextRole: EditText
    private lateinit var editTextFirstName: EditText
    private lateinit var editTextLastName: EditText
    private lateinit var editTextPhoneNumber: EditText
    private lateinit var editTextEmailAddress: EditText
    private lateinit var editTextCourse: EditText
    private lateinit var editTextDiv: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextRollNo: EditText
    private lateinit var signupbtn: TextView
    private lateinit var goToLogin: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Initialize EditTexts
        editTextFirstName = findViewById(R.id.editTextFirstName)
        editTextLastName = findViewById(R.id.editTextLastName)
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber)
        editTextEmailAddress = findViewById(R.id.editTextEmailAddress)
        editTextCourse = findViewById(R.id.editTextCourse)
        editTextDiv = findViewById(R.id.editTextDiv)
        editTextPassword = findViewById(R.id.editTextPassword)
        editTextRollNo = findViewById(R.id.editTextRollNo)
        editTextRole = findViewById(R.id.editTextRole)

        // Initialize Button and TextView
        signupbtn = findViewById(R.id.signupbtn)
        goToLogin = findViewById(R.id.goToLogin)

        signupbtn.setOnClickListener {
            lifecycleScope.launch {
                val firstName = editTextFirstName.text.toString()
                val lastName = editTextLastName.text.toString()
                val displayName = "$firstName $lastName"
                val phoneNumber = editTextPhoneNumber.text.toString()
                val email = editTextEmailAddress.text.toString()
                val course = editTextCourse.text.toString()
                val div = editTextDiv.text.toString()
                val role = editTextRole.text.toString()
                val password = editTextPassword.text.toString()
                val rollNo = editTextRollNo.text.toString()

                if (firstName.isNotEmpty() && lastName.isNotEmpty() &&
                    phoneNumber.isNotEmpty() && email.isNotEmpty() && course.isNotEmpty() &&
                    div.isNotEmpty() && role.isNotEmpty() && password.isNotEmpty() && rollNo.isNotEmpty()) {

                    val example = DataSignUp(
                        firstName, lastName, displayName , phoneNumber, email,
                        course, div, role, password, rollNo, false, true
                    )
                    signup(example)
                    val intent = Intent(this@SignUp, MainMenu::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    if (firstName.isEmpty()) editTextFirstName.error = "Please enter your first name"
                    if (lastName.isEmpty()) editTextLastName.error = "Please enter your last name"
                    if (phoneNumber.isEmpty()) editTextPhoneNumber.error = "Please enter your phone number"
                    if (email.isEmpty()) editTextEmailAddress.error = "Please enter your email"
                    if (course.isEmpty()) editTextCourse.error = "Please enter your course"
                    if (role.isEmpty()) editTextRole.error = "Please enter your role"
                    if (div.isEmpty()) editTextDiv.error = "Please enter your division"
                    if (password.isEmpty()) editTextPassword.error = "Please enter your password"
                    if (rollNo.isEmpty()) editTextRollNo.error = "Please enter your roll number"
                }
            }
        }

        goToLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private suspend fun signup(example: DataSignUp) {
        viewModelSignUp.signUp(example,this@SignUp)
    }
}
