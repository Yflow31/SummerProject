package com.somaiya.summer_project.applyform.Repository

import com.somaiya.summer_project.applyform.Model.ApplyFormData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RepositoryApplyForm {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val user = auth.currentUser
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun submitForm(form: ApplyFormData) {

        user?.let {
            firestore
                .collection("users")
                .document(it.uid)
                .collection("reasons")
                .add(form)
        }

        user?.let {
            val userid = it.uid

            firestore
                .collection("REASONS")
                .add(form)
        }

    }
}