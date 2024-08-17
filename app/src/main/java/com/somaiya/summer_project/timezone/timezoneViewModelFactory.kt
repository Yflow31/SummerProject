package com.somaiya.summer_project.timezone

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class timezoneViewModelFactory(private val repository: timezoneRepository, private val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(timezoneViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return timezoneViewModel(repository,application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
