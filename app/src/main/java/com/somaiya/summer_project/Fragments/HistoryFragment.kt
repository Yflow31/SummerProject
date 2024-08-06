package com.somaiya.summer_project.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.somaiya.summer_project.MainActivity
import com.somaiya.summer_project.R


class HistoryFragment : Fragment() {

    private lateinit var db:FirebaseFirestore
    private lateinit var db_timesLateDisplay: TextView
    private val user = Firebase.auth.currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        db = FirebaseFirestore.getInstance()
        db_timesLateDisplay = view.findViewById(R.id.db_timesLateDisplay)

        //Check if user is logged in or not
        if (user == null) {
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        //Displaying Times Late from firestore to db_timesLateDisplay

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