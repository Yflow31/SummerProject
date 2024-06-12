package com.somaiya.summer_project.login.Repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.somaiya.summer_project.login.Model.LoginResponse
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class RepositoryLogin {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _loginResponse = MutableLiveData<LoginResponse>()
    var loading:Boolean = false
    var success:Boolean = true
    //

    suspend fun login(email: String, password: String): MutableLiveData<LoginResponse> {
        loading = true
        success = false
        try {
//          Sign in with email and password
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    loading = false
                    success = true
                    _loginResponse.value = LoginResponse(loading,success,null)
                    Log.d("LoginRepository", "Login successful")
                } else {
                    loading = false
                    success = false
                    _loginResponse.value = LoginResponse(loading,success,null)
                    Log.e("LoginRepository", "Login failed", task.exception)
                }
            }
        } catch (e: Exception) {
            loading = false
            success = false
            _loginResponse.value = LoginResponse(loading,success,null)
            throw e
        }
        return _loginResponse
    }


}