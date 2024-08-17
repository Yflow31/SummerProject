package com.somaiya.summer_project.timezone

import com.somaiya.summer_project.timezone.retrofit.TimeApiService
import com.somaiya.summer_project.timezone.retrofit.TimeResponse

class timezoneRepository(private val apiService: TimeApiService) {

    suspend fun getTime(region: String, city: String): TimeResponse {
        return apiService.getTime(region, city)
    }
}
