package com.somaiya.summer_project.login.Repository

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactoryLogin(private val loginRepository: RepositoryLogin) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewModelLogin::class.java)) {
            return ViewModelLogin(loginRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}