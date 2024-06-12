package com.somaiya.summer_project.signup.Repository

import RepositorySignUp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactorySignUp(val repositorySignUp: RepositorySignUp): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewModelSignUp::class.java)) {
            return ViewModelSignUp(repositorySignUp) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}