package com.somaiya.summer_project.pofileupdate.Repository

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactoryProfile(private val profileRepository: RepositoryProfile): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewModelProfile::class.java)) {
            return ViewModelProfile(profileRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}