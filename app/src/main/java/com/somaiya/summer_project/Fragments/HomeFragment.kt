package com.somaiya.summer_project.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.somaiya.summer_project.R
import com.somaiya.summer_project.ApplyForForm
import com.somaiya.summer_project.RecyclerReasons.MyAdapter
import com.somaiya.summer_project.applyform.Model.ApplyFormData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.somaiya.summer_project.RecyclerReasons.ApprovalListener

class HomeFragment : Fragment() , ApprovalListener {

    private lateinit var recyclerviewreason: RecyclerView
    private lateinit var applyform: ArrayList<ApplyFormData>
    private lateinit var reasonAdapter: MyAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerviewreason = view.findViewById(R.id.recyclerviewreason)
        recyclerviewreason.layoutManager = LinearLayoutManager(context)
        applyform = arrayListOf()
        reasonAdapter = MyAdapter(applyform,this)
        recyclerviewreason.adapter = reasonAdapter

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userDocRef = db.collection("USERS").document(currentUser.uid)
            userDocRef.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val role = documentSnapshot.getString("role")
                    if (role != null) {
                        when (role) {
                            "admin", "teacher" -> {
                                db.collection("REASONS").get()
                                    .addOnSuccessListener { querySnapshot ->
                                        for (document in querySnapshot.documents) {
                                            val location = document.getString("location")
                                            val reasonForBeingLate = document.getString("reasonForBeingLate")
                                            val timesLate = document.getString("timesLate")
                                            val userEmail = document.getString("userEmail")
                                            val checkboxChecked = document.getBoolean("checkboxChecked")
                                            val reasonId = document.getString("reasonId")

                                            if (location != null && reasonForBeingLate != null && timesLate != null && userEmail != null) {
                                                val formData = ApplyFormData(
                                                    reasonForBeingLate,
                                                    location,
                                                    timesLate,
                                                    userEmail,
                                                    isCheckboxChecked = checkboxChecked ?: false,
                                                    reasonId = reasonId ?: ""
                                                )
                                                applyform.add(formData)
                                            }
                                        }
                                        reasonAdapter.notifyDataSetChanged()
                                    }
                            }

                            "student" -> {
                                db.collection("users").document(currentUser.uid)
                                    .collection("reasons").get()
                                    .addOnSuccessListener { querySnapshot ->
                                        for (document in querySnapshot.documents) {
                                            val userEmail = document.getString("email")
                                            val location = document.getString("location")
                                            val reasonForBeingLate = document.getString("reasonForBeingLate")
                                            val timesLate = document.getString("timesLate")
                                            val checkboxChecked = document.getBoolean("checkboxChecked")
                                            val reasonId = document.getString("reasonId")


                                            if (location != null && reasonForBeingLate != null && timesLate != null && userEmail != null) {
                                                val formData = ApplyFormData(
                                                    reasonForBeingLate = reasonForBeingLate, // Use named parameters for clarity
                                                    location = location,
                                                    timesLate = timesLate,
                                                    email = userEmail,
                                                    isCheckboxChecked = checkboxChecked ?: false,
                                                    reasonId = reasonId ?: ""
                                                )
                                                applyform.add(formData)
                                            }
                                        }
                                        reasonAdapter.notifyDataSetChanged()
                                    }
                            }
                        }
                    } else {
                        Log.d("UserRole", "Role field is missing")
                    }
                } else {
                    Log.d("UserDoc", "User document does not exist")
                }
            }.addOnFailureListener { e ->
                Log.e("FirebaseError", "Error fetching user document", e)
            }
        }



        val fab = view.findViewById<View>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(activity, ApplyForForm::class.java)
            startActivity(intent)
        }
        return view
    }

    override fun onApprovalResult(isApproved: Boolean,position: Int, reasonId: String) {
        Log.d("ischecked", "{$reasonId}")
        // Remove the item from the list

        // Update the isChecked field in Firestore
        db.collection("REASONS")
            .document(reasonId)
            .get()
            .addOnSuccessListener {
                it.reference.update("checkboxChecked", isApproved)
                applyform.removeAt(position)
                reasonAdapter.notifyItemRemoved(position)
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting documents: ", exception)
            }
    }
}
