package com.somaiya.summer_project.signup.Repository

import RepositorySignUp
import android.content.Context
import androidx.lifecycle.ViewModel
import com.somaiya.summer_project.signup.Model.DataSignUp

class ViewModelSignUp(val signUpRepository: RepositorySignUp): ViewModel() {

    suspend fun signUp(dataSignUp: DataSignUp, context: Context) {
        signUpRepository.signUp(dataSignUp,context)
    }

}