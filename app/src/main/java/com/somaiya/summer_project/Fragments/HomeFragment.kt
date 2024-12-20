package com.somaiya.summer_project.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.somaiya.summer_project.R
import com.somaiya.summer_project.ApplyForForm
import com.somaiya.summer_project.RecyclerReasons.MyAdapter
import com.somaiya.summer_project.applyform.Model.ApplyFormData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.somaiya.summer_project.RecyclerReasons.ApprovalListener
import com.somaiya.summer_project.utils.ApprovalConstant
import com.somaiya.summer_project.utils.Loader
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeFragment : Fragment(), ApprovalListener {

    //Loaders
    private var LOADER_SHOWING = false
    private var loadingInsuranceDialogueFragment: Loader? = null

    private lateinit var recyclerviewreason: RecyclerView
    private lateinit var applyform: ArrayList<ApplyFormData>
    private lateinit var reasonAdapter: MyAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var main: LinearLayout
    private lateinit var SwipeRefreshLayout: SwipeRefreshLayout


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        main = view.findViewById<LinearLayout>(R.id.Home_Main)

        //Show Loader
        showLoadingMain(main)

        //Swipe Refresh
        SwipeRefreshLayout = view.findViewById(R.id.SwipeRefreshLayout)

        recyclerviewreason = view.findViewById(R.id.recyclerviewreason)
        recyclerviewreason.layoutManager = LinearLayoutManager(context)
        applyform = arrayListOf()
        reasonAdapter = MyAdapter(applyform, this)
        recyclerviewreason.adapter = reasonAdapter

        val searchView = view.findViewById<SearchView>(R.id.searchView)

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
                                searchView.visibility = View.VISIBLE
                                db.collection("ReasonsForAdmin")
                                    .orderBy("approvalStatus", Query.Direction.DESCENDING)
                                    .get()
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
                                            val selectedTimeSlot =
                                                document.getString("selectedTimeSlot")


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

                                        val sortedList = applyform.sortedWith(compareBy<ApplyFormData> {
                                            when (it.approvalStatus) {
                                                ApprovalConstant.PENDING.name -> 1
                                                ApprovalConstant.ACCEPTED.name -> 2
                                                ApprovalConstant.REJECTED.name -> 3
                                                else -> 4 // Default for unexpected values
                                            }
                                        }.thenBy { it.email }
                                            .thenByDescending { it.timesLate.toInt() }
                                            .thenByDescending {
                                                // Assuming `currentdate` is a String field in your ApplyFormData class
                                                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(it.currentdate)
                                            }.thenByDescending {
                                                SimpleDateFormat("mm:HH", Locale.getDefault()).parse(it.currenttime)
                                            }
                                        )

                                        applyform.clear()
                                        applyform.addAll(sortedList)
                                        reasonAdapter.notifyDataSetChanged()
                                        hideLoadingMain(main)
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
                                            val selectedTimeSlot =
                                                document.getString("selectedTimeSlot")


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
                                        val sortedList = applyform.sortedWith(compareBy<ApplyFormData> {
                                            when (it.approvalStatus) {
                                                ApprovalConstant.PENDING.name -> 1
                                                ApprovalConstant.ACCEPTED.name -> 2
                                                ApprovalConstant.REJECTED.name -> 3
                                                else -> 4 // Default for unexpected values
                                            }
                                        }.thenBy { it.email }
                                            .thenByDescending { it.timesLate.toInt() }
                                            .thenByDescending {
                                                // Assuming `currentdate` is a String field in your ApplyFormData class
                                                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(it.currentdate)
                                            }.thenByDescending {
                                                SimpleDateFormat("mm:HH", Locale.getDefault()).parse(it.currenttime)
                                            }
                                        )

                                        applyform.clear()
                                        applyform.addAll(sortedList)
                                        reasonAdapter.notifyDataSetChanged()
                                        hideLoadingMain(main)
                                    }
                            }
                        }
                    } else {
                        Toast.makeText(context, "User Role is Invalid", Toast.LENGTH_SHORT).show()
                        hideLoadingMain(main)
                    }
                } else {
                    Toast.makeText(context, "User document does not exist", Toast.LENGTH_SHORT).show()
                    hideLoadingMain(main)
                }
            }.addOnFailureListener { e ->
                Log.e("FirebaseError", "Error fetching user document", e)
            }
        }

        SwipeRefreshLayout.setOnRefreshListener {
            SwipeRefreshLayout.isRefreshing = false
            if (currentUser != null) {
                val userDocRef = db.collection("USERS").document(currentUser.uid)
                userDocRef.get().addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val role = documentSnapshot.getString("role")
                        if (role != null) {
                            when (role) {
                                "admin" -> {
                                    searchView.visibility = View.VISIBLE
                                    db.collection("ReasonsForAdmin")
                                        .orderBy("approvalStatus", Query.Direction.DESCENDING)
                                        .get()
                                        .addOnSuccessListener { querySnapshot ->
                                            applyform.clear()
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
                                                val selectedTimeSlot =
                                                    document.getString("selectedTimeSlot")


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

                                            val sortedList = applyform.sortedWith(compareBy<ApplyFormData> {
                                                when (it.approvalStatus) {
                                                    ApprovalConstant.PENDING.name -> 1
                                                    ApprovalConstant.ACCEPTED.name -> 2
                                                    ApprovalConstant.REJECTED.name -> 3
                                                    else -> 4 // Default for unexpected values
                                                }
                                            }.thenBy { it.email }
                                                .thenByDescending { it.timesLate.toInt() }
                                                .thenByDescending {
                                                    // Assuming `currentdate` is a String field in your ApplyFormData class
                                                    SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(it.currentdate)
                                                }.thenByDescending {
                                                    SimpleDateFormat("mm:HH", Locale.getDefault()).parse(it.currenttime)
                                                }
                                            )

                                            applyform.clear()
                                            applyform.addAll(sortedList)
                                            reasonAdapter.notifyDataSetChanged()
                                            hideLoadingMain(main)
                                        }
                                }

                                "student" -> {
                                    db.collection("USERS").document(currentUser.uid)
                                        .collection("reasons").get()
                                        .addOnSuccessListener { querySnapshot ->
                                            applyform.clear()
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
                                                val selectedTimeSlot =
                                                    document.getString("selectedTimeSlot")


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
                                            val sortedList = applyform.sortedWith(compareBy<ApplyFormData> {
                                                when (it.approvalStatus) {
                                                    ApprovalConstant.PENDING.name -> 1
                                                    ApprovalConstant.ACCEPTED.name -> 2
                                                    ApprovalConstant.REJECTED.name -> 3
                                                    else -> 4 // Default for unexpected values
                                                }
                                            }.thenBy { it.email }
                                                .thenByDescending { it.timesLate.toInt() }
                                                .thenByDescending {
                                                    // Assuming `currentdate` is a String field in your ApplyFormData class
                                                    SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(it.currentdate)
                                                }.thenByDescending {
                                                    SimpleDateFormat("mm:HH", Locale.getDefault()).parse(it.currenttime)
                                                }
                                            )

                                            applyform.clear()
                                            applyform.addAll(sortedList)
                                            reasonAdapter.notifyDataSetChanged()
                                            hideLoadingMain(main)
                                        }
                                }
                            }
                        } else {
                            Toast.makeText(context, "User Role is Invalid", Toast.LENGTH_SHORT).show()
                            hideLoadingMain(main)
                        }
                    } else {
                        Toast.makeText(context, "User document does not exist", Toast.LENGTH_SHORT).show()
                        hideLoadingMain(main)
                    }
                }.addOnFailureListener { e ->
                    Log.e("FirebaseError", "Error fetching user document", e)
                }
            }
        }


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText ?: "")
                return true
            }
        })



        val fab = view.findViewById<View>(R.id.fab)
        fab.setOnClickListener {
            currentUser?.reload()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val isVerified = currentUser.isEmailVerified  // Check if the email is verified
                    if (isVerified) {
                        // Email is verified, proceed with your logic
                        db.collection("USERS").document(currentUser.uid).update("isactive", true)
                        val intent = Intent(activity, ApplyForForm::class.java)
                        startActivity(intent)

                    } else {
                        currentUser.sendEmailVerification().addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(
                                    context,
                                    "We have sent you a verification link via email. Please verify your account.",
                                    Toast.LENGTH_SHORT
                                ).show();
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Please Check your email",
                                    Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                    }
                }
            }
        }
        return view
    }


    private fun filterList(query: String) {

            val filteredList = ArrayList<ApplyFormData>()

            // Filter based on email
            for (item in applyform) {
                if (item.email.lowercase().contains(query.lowercase())) {
                    filteredList.add(item)
                }
            }

            // Update the adapter with the filtered list
            if (filteredList.isEmpty()) {
            } else {
                reasonAdapter.setFilteredList(filteredList)
            }
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
                        db.collection("USERS").document(fetchedUserId)
                            .update("canCreateNewReason", true)
                        db.collection("ReasonsForAdmin").document(reasonId)
                            .update("canCreateNewReason", true)

                        //Removing Active Status
                        db.collection("USERS").document(fetchedUserId)
                            .update("isactive", false)

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

    override fun onDeleteResult(position: Int, reasonId: String) {
        showLoadingMain(main)

        db.collection("ReasonsForAdmin").document(reasonId).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val fetchedUserId = documentSnapshot.getString("userId")

                    // Check if the fetched userId is not null
                    if (fetchedUserId != null) {
                        // Delete the document from ReasonsForAdmin
                        db.collection("ReasonsForAdmin").document(reasonId)
                            .delete()
                            .addOnSuccessListener {
                                // After successful deletion from ReasonsForAdmin
                                db.collection("USERS").document(fetchedUserId)
                                    .collection("reasons").document(reasonId)
                                    .delete()
                                    .addOnSuccessListener {
                                        applyform.removeAt(position)
                                        reasonAdapter.notifyItemRemoved(position)

                                        // Check if the user has any remaining active requests
                                        db.collection("USERS").document(fetchedUserId)
                                            .collection("reasons")
                                            .whereEqualTo("isactive", true)
                                            .get()
                                            .addOnSuccessListener { querySnapshot ->
                                                val hasActiveRequests = !querySnapshot.isEmpty

                                                // Update canCreateNewReason based on the presence of active requests
                                                db.collection("USERS").document(fetchedUserId)
                                                    .update("canCreateNewReason", !hasActiveRequests)
                                                    .addOnSuccessListener {
                                                        hideLoadingMain(main)
                                                    }
                                                    .addOnFailureListener { e ->
                                                        Log.w("UpdateError", "Error updating canCreateNewReason", e)
                                                        hideLoadingMain(main)
                                                    }
                                            }
                                            .addOnFailureListener { e ->
                                                Log.w("QueryError", "Error checking active requests", e)
                                                hideLoadingMain(main)
                                            }
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w("DeleteError", "Error deleting document from USERS", e)
                                        hideLoadingMain(main)
                                    }
                            }
                            .addOnFailureListener { e ->
                                Log.w("DeleteError", "Error deleting document from ReasonsForAdmin", e)
                                hideLoadingMain(main)
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.w("FetchError", "Error fetching document from ReasonsForAdmin", e)
                hideLoadingMain(main)
            }
    }




    private fun showLoadingMain(main: LinearLayout? = null) {
        if (!LOADER_SHOWING) {
            LOADER_SHOWING = true

            val fragmentManager = requireActivity().supportFragmentManager
            val fragment = fragmentManager.findFragmentByTag("loadingDialog") as Loader?

            if (fragment == null) {
                // No existing fragment found, create and show a new instance
                loadingInsuranceDialogueFragment = Loader.newInstance("Loading, Please wait...")
                loadingInsuranceDialogueFragment?.isCancelable = false
                loadingInsuranceDialogueFragment?.show(fragmentManager, "loadingDialog")
                main?.visibility = View.GONE
            } else if (!fragment.isAdded) {
                // If the fragment exists but hasn't been added, show it again.
                // This scenario is rare due to the lifecycle of DialogFragment.
                fragment.show(fragmentManager, "loadingDialog")
                main?.visibility = View.GONE
            }
            // If the fragment is already added, it should be visible and nothing needs to be done.
        }
    }

    private fun hideLoadingMain(main: LinearLayout? = null) {
        try {
            LOADER_SHOWING = false

            val fragmentManager = requireActivity().supportFragmentManager
            val fragment = fragmentManager.findFragmentByTag("loadingDialog") as Loader?
            if (fragment != null && fragment.isAdded) {
                fragment.dismiss()
                main?.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
