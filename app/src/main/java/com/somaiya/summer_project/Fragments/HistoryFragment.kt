package com.somaiya.summer_project.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.somaiya.summer_project.MainActivity
import com.somaiya.summer_project.R


class HistoryFragment : Fragment() {

    private lateinit var db:FirebaseFirestore
    private lateinit var db_timesLateDisplay: TextView
    private val user = Firebase.auth.currentUser
    private lateinit var cd_profile: androidx.cardview.widget.CardView
    private lateinit var cd_dashboard: androidx.cardview.widget.CardView
    private lateinit var cd_home: androidx.cardview.widget.CardView
    private lateinit var cd_logout: androidx.cardview.widget.CardView

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        db = FirebaseFirestore.getInstance()
        db_timesLateDisplay = view.findViewById(R.id.db_timesLateDisplay)
        cd_profile = view.findViewById(R.id.cd_profile)
        cd_dashboard = view.findViewById(R.id.cd_dashboard)
        cd_home = view.findViewById(R.id.cd_home)
        cd_logout = view.findViewById(R.id.cd_logout)

        //Check if user is logged in or not
        if (user == null) {
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        //Displaying Times Late from firestore to db_timesLateDisplay

        val navHostFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val homeToProfileaction = HistoryFragmentDirections.actionHistoryFragmentToProfileFragment()
        cd_profile.setOnClickListener {
            navController.navigate(homeToProfileaction)
        }

        val homeToDashboardaction = HistoryFragmentDirections.actionHistoryFragmentToHomeFragment()
        cd_dashboard.setOnClickListener {
            navController.navigate(homeToDashboardaction)
        }

        cd_home.setOnClickListener {
            Toast.makeText(requireContext(), "You are already at home", Toast.LENGTH_SHORT).show()
        }

        cd_logout.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }



        db.collection("USERS").document(user?.uid ?: "")
            .collection("reasons")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val count = task.result.size()
                    db_timesLateDisplay.text = count.toString()
                    Log.d("db_timesLateDisplay", "onCreate:"+ db_timesLateDisplay.text )
                } else {
                    Log.w("FirestoreCount", "Error getting documents.", task.exception)
                }
            }


        return view
    }

}