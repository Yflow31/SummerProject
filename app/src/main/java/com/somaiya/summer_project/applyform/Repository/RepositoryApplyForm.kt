package com.somaiya.summer_project.applyform.Repository

import android.util.Log
import com.somaiya.summer_project.applyform.Model.ApplyFormData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class RepositoryApplyForm {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val user = auth.currentUser
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun submitForm(form: ApplyFormData) {

        val reasonId = firestore.collection("ReasonsForAdmin").document().id
        val role = firestore.collection("USERS").document(user?.uid ?: "").get().await().getString("role")

        firestore.collection("USERS").document(user?.uid ?: "").update("canCreateNewReason", false)
        firestore.collection("USERS").document(user?.uid ?: "").update("isactive", true)

        if (role != null) {
            form.role = role
        }
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
                .collection("ReasonsForAdmin")
                .document(reasonId)
                .set(form)
        }

    }
}