package com.somaiya.summer_project.utils

import android.content.Context
import android.content.SharedPreferences
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

public class UtiliMethods {
     companion object{
         fun storeTimestampInSharedPreferences(context: Context, dateTimeString: String) {
             // Parse the string to a ZonedDateTime object
             val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
             val zonedDateTime = ZonedDateTime.parse(dateTimeString, formatter)

             // Convert to milliseconds since epoch
             val millis = zonedDateTime.toInstant().toEpochMilli()

             // Get SharedPreferences instance
             val sharedPreferences: SharedPreferences = context.getSharedPreferences("GetDateTimePrefs", Context.MODE_PRIVATE)

             // Store the timestamp in SharedPreferences
             val editor = sharedPreferences.edit()
             editor.putLong("timestampMillis", millis)
             editor.apply() // Or editor.commit() to save synchronously
         }

         fun getTimestampFromSharedPreferences(context: Context): Long {
             // Get SharedPreferences instance
             val sharedPreferences: SharedPreferences = context.getSharedPreferences("GetDateTimePrefs", Context.MODE_PRIVATE)

             // Retrieve the timestamp
             return sharedPreferences.getLong("timestampMillis", 0) // Default value is 0 if not found
         }


     }
}