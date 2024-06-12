package com.somaiya.summer_project.applyform.Repository

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactoryApplyForm(private val applyFormRepository: RepositoryApplyForm) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewModelApplyForm::class.java)) {
            return ViewModelApplyForm(applyFormRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}