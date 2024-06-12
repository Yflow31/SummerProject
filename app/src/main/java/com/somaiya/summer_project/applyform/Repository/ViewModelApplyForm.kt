package com.somaiya.summer_project.applyform.Repository

import androidx.lifecycle.ViewModel
import com.somaiya.summer_project.applyform.Model.ApplyFormData

class ViewModelApplyForm(private val applyFormRepository: RepositoryApplyForm) : ViewModel() {

    suspend fun submitForm(form: ApplyFormData) {
        applyFormRepository.submitForm(form)
    }

}