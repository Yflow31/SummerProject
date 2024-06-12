package com.somaiya.summer_project.login.Repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.somaiya.summer_project.login.Model.LoginResponse

class ViewModelLogin(private val loginRepository: RepositoryLogin): ViewModel() {

    suspend fun login(email: String, password: String):LiveData<LoginResponse> {
        return loginRepository.login(email, password)
    }
}