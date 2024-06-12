package com.somaiya.summer_project.pofileupdate.Repository

import androidx.lifecycle.ViewModel
import com.somaiya.summer_project.pofileupdate.Model.ProfileData

class ViewModelProfile(private val profileRepository: RepositoryProfile): ViewModel() {

    suspend fun updateProfile(profileupdate: ProfileData) {
        profileRepository.updateProfile(profileupdate)
    }
}