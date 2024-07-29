package com.somaiya.summer_project.applyform.Repository

import com.somaiya.summer_project.applyform.Model.ApplyFormData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RepositoryApplyForm {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val user = auth.currentUser
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun submitForm(form: ApplyFormData) {

        val reasonId = firestore.collection("REASONS").document().id
        form.reasonId = reasonId

        user?.let {

            firestore
                .collection("USERS")
                .document(it.uid)
                .collection("reasons")
                .document(reasonId)
                .set(form)
        }

        user?.let {
            firestore
                .collection("REASONS")
                .document(reasonId)
                .set(form)
        }

    }
}