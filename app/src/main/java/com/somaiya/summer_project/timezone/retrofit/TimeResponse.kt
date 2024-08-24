package com.somaiya.summer_project.timezone.retrofit

data class TimeResponse(
    val utc_offset: String,
    val timezone: String,
    val day_of_week: Int,
    val day_of_year: Int,
    val datetime: String,
    val utc_datetime: String,
    val unixtime: Long,
    val raw_offset: Int,
    val week_number: Int,
    val dst: Boolean,
    val abbreviation: String,
    val dst_offset: Int,
    val dst_from: String?,
    val dst_until: String?,
    val client_ip: String
)
