package com.somaiya.summer_project.timezone.retrofit

import retrofit2.http.GET
import retrofit2.http.Path

interface TimeApiService {
    @GET("timezone/{region}/{city}")
    suspend fun getTime(
        @Path("region") region: String,
        @Path("city") city: String
    ): TimeResponse
}