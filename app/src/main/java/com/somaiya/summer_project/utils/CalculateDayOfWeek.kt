package com.somaiya.summer_project.utils

import java.text.SimpleDateFormat
import java.util.*

class CalculateDayOfWeek {

        fun calculateDayOfWeek(dateString: String): Int {
            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val date = dateFormat.parse(dateString)

            val calendar = Calendar.getInstance()
            if (date != null) {
                calendar.time = date
            }

            // Get the day of the week (1 = Sunday, 2 = Monday, ..., 7 = Saturday)
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

            // Adjust to make Monday = 0, ..., Sunday = 6
            return when (dayOfWeek) {
                Calendar.SUNDAY -> 6
                else -> dayOfWeek - 2
            }
        }

}
