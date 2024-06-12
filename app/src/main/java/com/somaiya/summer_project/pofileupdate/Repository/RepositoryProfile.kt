package com.somaiya.summer_project.pofileupdate.Repository

import com.somaiya.summer_project.pofileupdate.Model.ProfileData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RepositoryProfile {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val user = auth.currentUser
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun updateProfile(form: ProfileData) {


    }
}