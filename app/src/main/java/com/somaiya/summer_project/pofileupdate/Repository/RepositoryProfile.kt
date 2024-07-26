package com.somaiya.summer_project.pofileupdate.Repository

import android.util.Log
import com.somaiya.summer_project.pofileupdate.Model.ProfileData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class RepositoryProfile {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val user = auth.currentUser
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun updateProfile(form: ProfileData) {
        user?.let {
            val userRef = firestore.collection("USERS").document(it.uid)
            val profileData = hashMapOf(
                "firstName" to form.firstName,
                "lastName" to form.lastName,
                "displayName" to form.displayName,
                "phoneNumber" to form.phoneNumber,
                "course" to form.course,
                "div" to form.div,
                "role" to form.role,
                "rollNo" to form.rollNo
            )

            userRef.set(profileData, SetOptions.merge())
                .addOnSuccessListener {
                    // Profile updated successfully
                    // You can add a callback or log a success message here
                    Log.d("RepositoryProfile", "updateProfile:{$profileData} ")
                }
                .addOnFailureListener { e ->
                    // Handle the error
                    // You can add a callback or log an error message here
                }
        }
    }
}