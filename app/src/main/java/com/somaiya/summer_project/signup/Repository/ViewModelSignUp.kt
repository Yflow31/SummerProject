package com.somaiya.summer_project.signup.Repository

import RepositorySignUp
import androidx.lifecycle.ViewModel
import com.somaiya.summer_project.signup.Model.DataSignUp

class ViewModelSignUp(val signUpRepository: RepositorySignUp): ViewModel() {

    suspend fun signUp(dataSignUp: DataSignUp) {
        signUpRepository.signUp(dataSignUp)
    }

}