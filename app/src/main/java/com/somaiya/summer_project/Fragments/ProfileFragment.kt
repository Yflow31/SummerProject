package com.somaiya.summer_project.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        val main = view.findViewById<LinearLayout>(R.id.main)
        //Show Loader
        showLoadingMain(main)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser



        val firstnametxt = view.findViewById<TextView>(R.id.firstnametxt)
        val lastnametxt = view.findViewById<TextView>(R.id.lastnametxt)
        val displaynametxt = view.findViewById<TextView>(R.id.displaynametxt)
        val phonenotxt = view.findViewById<TextView>(R.id.phonenotxt)
        val coursetxt = view.findViewById<TextView>(R.id.coursetxt)
        val divtxt = view.findViewById<TextView>(R.id.divtxt)
        val roletxt = view.findViewById<TextView>(R.id.roletxt)
        val rollnotxt = view.findViewById<TextView>(R.id.rollnotxt)
        val backbtn = view.findViewById<TextView>(R.id.backbtn)
        val profileUpdate = view.findViewById<TextView>(R.id.profileUpdate)

        if (currentUser != null) {
            val userDocRef = db.collection("USERS").document(currentUser.uid)
            userDocRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val userDataMap = document.data
                        if (userDataMap != null) {
                            firstnametxt?.text = userDataMap["firstName"] as String?
                            lastnametxt?.text = userDataMap["lastName"] as String?
                            displaynametxt?.text = userDataMap["displayName"] as String?
                            phonenotxt?.text = userDataMap["phoneNumber"] as String?
                            coursetxt?.text = userDataMap["course"] as String?
                            divtxt?.text = userDataMap["div"] as String?
                            roletxt?.text = userDataMap["role"] as String?
                            rollnotxt?.text = userDataMap["rollNo"] as String?

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

        profileUpdate?.setOnClickListener {
            val intent = Intent(activity, ProfileUpdate::class.java)
            startActivity(intent)
        }

        backbtn?.setOnClickListener {
            val intent = Intent(activity, MainMenu::class.java)
            startActivity(intent)
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
