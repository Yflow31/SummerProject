package com.somaiya.summer_project.timezone

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.somaiya.summer_project.timezone.retrofit.TimeResponse
import com.somaiya.summer_project.utils.UtiliMethods
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class timezoneViewModel(private val repository: timezoneRepository, val application: Application) : ViewModel() {


    private var timeJob: Job? = null

    // Function to start the periodic API call
    fun startFetchingTimePeriodically(region: String, city: String) {
        // Cancel any existing job
        timeJob?.cancel()

        // Start a new job to fetch time every 3 seconds
        timeJob = viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                try {
                    val response = repository.getTime(region, city)
                    Log.d("timezone", "getTime from repository: ${response.datetime}")
                    UtiliMethods.storeTimestampInSharedPreferences(application, response.datetime)
                } catch (e: Exception) {
                    Log.d("timezone", "Error fetching time: ${e.message}")

                }
                delay(30000) // Delay for 30 seconds
            }
        }
    }

    // Function to stop the periodic API call
    fun stopFetchingTime() {
        timeJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        // Clean up the job when the ViewModel is cleared
        timeJob?.cancel()
    }
}