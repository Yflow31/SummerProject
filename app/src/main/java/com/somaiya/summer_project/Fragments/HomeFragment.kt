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
import com.somaiya.summer_project.utils.ApprovalConstant
import java.util.Calendar

class HomeFragment : Fragment(), ApprovalListener {

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
        reasonAdapter = MyAdapter(applyform, this)
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
                            "admin" -> {
                                db.collection("ReasonsForAdmin").get()
                                    .addOnSuccessListener { querySnapshot ->
                                        for (document in querySnapshot.documents) {

                                            val userEmail = document.getString("email")
                                            val location = document.getString("location")
                                            val reasonForBeingLate =
                                                document.getString("reasonForBeingLate")
                                            val timesLate = document.getString("timesLate")
                                            val reasonId = document.getString("reasonId")
                                            val approvalStatus =
                                                document.getString("approvalStatus")
                                            val date = document.getString("currentdate")
                                            val time = document.getString("currenttime")
                                            val subject = document.getString("subject")
                                            val faculty = document.getString("faculty")
                                            val selectedTimeSlot = document.getString("selectedTimeSlot")


                                            if (location != null && reasonForBeingLate != null && timesLate != null && userEmail != null) {
                                                val formData = ApplyFormData(
                                                    reasonForBeingLate = reasonForBeingLate, // Use named parameters for clarity
                                                    location = location,
                                                    timesLate = timesLate,
                                                    email = userEmail,
                                                    reasonId = reasonId ?: "",
                                                    approvalStatus = approvalStatus
                                                        ?: ApprovalConstant.PENDING.name,
                                                    role = role,
                                                    currentdate = date ?: "",
                                                    currenttime = time ?: "",
                                                    subject = subject ?: "",
                                                    faculty = faculty ?: "",
                                                    selectedTimeSlot = selectedTimeSlot ?: ""
                                                )
                                                applyform.add(formData)
                                            }
                                        }
                                        reasonAdapter.notifyDataSetChanged()
                                    }
                            }

                            "student" -> {
                                db.collection("USERS").document(currentUser.uid)
                                    .collection("reasons").get()
                                    .addOnSuccessListener { querySnapshot ->
                                        for (document in querySnapshot.documents) {
                                            val userEmail = document.getString("email")
                                            val location = document.getString("location")
                                            val reasonForBeingLate =
                                                document.getString("reasonForBeingLate")
                                            val timesLate = document.getString("timesLate")
                                            val reasonId = document.getString("reasonId")
                                            val approvalStatus =
                                                document.getString("approvalStatus")
                                            val date = document.getString("currentdate")
                                            val time = document.getString("currenttime")
                                            val subject = document.getString("subject")
                                            val faculty = document.getString("faculty")
                                            val selectedTimeSlot = document.getString("selectedTimeSlot")


                                            if (location != null && reasonForBeingLate != null && timesLate != null && userEmail != null) {
                                                val formData = ApplyFormData(
                                                    reasonForBeingLate = reasonForBeingLate, // Use named parameters for clarity
                                                    location = location,
                                                    timesLate = timesLate,
                                                    email = userEmail,
                                                    reasonId = reasonId ?: "",
                                                    approvalStatus = approvalStatus
                                                        ?: ApprovalConstant.PENDING.name,
                                                    role = role,
                                                    currentdate = date ?: "",
                                                    currenttime = time ?: "",
                                                    subject = subject ?: "",
                                                    faculty = faculty ?: "",
                                                    selectedTimeSlot = selectedTimeSlot ?: ""
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

    override fun onApprovalResult(isApproved: Boolean, position: Int, reasonId: String) {
        var status: String = ApprovalConstant.PENDING.name

        if (isApproved) {
            status = ApprovalConstant.ACCEPTED.name
        } else {
            status = ApprovalConstant.REJECTED.name
        }

        // First, fetch the userId from the ReasonsForAdmin document
        db.collection("ReasonsForAdmin").document(reasonId).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val fetchedUserId = documentSnapshot.getString("userId")

                    // Check if the fetched userId is not null
                    if (fetchedUserId != null) {
                        // Update the approval status in the ReasonsForAdmin collection
                        db.collection("ReasonsForAdmin").document(reasonId)
                            .update("approvalStatus", status)
                            .addOnSuccessListener {
                                applyform[position].approvalStatus = status
                                reasonAdapter.notifyItemChanged(position)
                            }
                            .addOnFailureListener { exception ->
                                Log.w("Firestore", "Error updating ReasonsForAdmin: ", exception)
                            }

                        //Update Can Create function
                        db.collection("USERS").document(fetchedUserId).update("canCreateNewReason", false)
                        db.collection("ReasonsForAdmin").document(reasonId).update("canCreateNewReason", false)

                        // Update the approval status in the USERS collection
                        db.collection("USERS").document(fetchedUserId)
                            .collection("reasons").document(reasonId)
                            .update("approvalStatus", status)
                            .addOnFailureListener { exception ->
                                Log.w("Firestore", "Error updating USERS reasons: ", exception)
                            }
                    } else {
                        Log.w("Firestore", "userId not found in ReasonsForAdmin document")
                    }
                } else {
                    Log.w("Firestore", "Reason document not found in ReasonsForAdmin")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error fetching ReasonsForAdmin document: ", exception)
            }
    }
}
