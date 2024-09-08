package com.somaiya.summer_project.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.somaiya.summer_project.MainActivity
import com.somaiya.summer_project.MainMenu
import com.somaiya.summer_project.ProfileUpdate
import com.somaiya.summer_project.R
import com.somaiya.summer_project.utils.Loader


class ProfileFragment : Fragment() {

    //Loaders
    private var LOADER_SHOWING = false
    private var loadingInsuranceDialogueFragment: Loader? = null

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var firstLastNameTextView: TextView
    private lateinit var rollNoTextView: TextView
    private lateinit var timesLateCountTextView: TextView
    private lateinit var rejectTextTextView: TextView
    private lateinit var acceptCountTextView: TextView
    private lateinit var rejectCountTextView: TextView
    private lateinit var roleTextView: TextView
    private lateinit var courseTextView: TextView
    private lateinit var phoneTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var main: LinearLayout

    private lateinit var profile_button: ImageButton
    private lateinit var logout: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        main = view.findViewById<LinearLayout>(R.id.main)
        //Show Loader
        showLoadingMain(main)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser

        firstLastNameTextView = view.findViewById(R.id.first_last_name)

        rollNoTextView = view.findViewById(R.id.roll_no)

        timesLateCountTextView = view.findViewById(R.id.times_late_count)
        rejectTextTextView = view.findViewById(R.id.reject_text)
        acceptCountTextView = view.findViewById(R.id.accept_count)

        roleTextView = view.findViewById(R.id.role_text)
        courseTextView = view.findViewById(R.id.course_text)
        phoneTextView = view.findViewById(R.id.phone_text)
        emailTextView = view.findViewById(R.id.email_text)

        profile_button = view.findViewById(R.id.profile_button)
        logout = view.findViewById(R.id.logout)


        if (currentUser != null) {
            val userDocRef = db.collection("USERS").document(currentUser.uid)
            userDocRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val userDataMap = document.data
                        if (userDataMap != null) {
                            firstLastNameTextView.text = (userDataMap["firstName"] as String?) +" "+ (userDataMap["lastName"] as String?)
                            phoneTextView.text = "+91 " + userDataMap["phoneNumber"] as String?
                            courseTextView?.text = userDataMap["course"] as String? + "-" + userDataMap["div"] as String?
                            roleTextView.text = userDataMap["role"] as String?
                            rollNoTextView.text = userDataMap["rollNo"] as String?
                            emailTextView.text = userDataMap["email"] as String?

                            hideLoadingMain(main)
                        }
                    } else {
                        Log.d("ProfileFragment", "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("ProfileFragment", "get failed with ", exception)
                }
        } else {
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }

        profile_button.setOnClickListener {
            val intent = Intent(activity, ProfileUpdate::class.java)
            startActivity(intent)
            activity?.finish()
        }

        logout.setOnClickListener {
            auth.signOut()
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        return view
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
