package com.somaiya.summer_project.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.somaiya.summer_project.ProfileUpdate
import com.somaiya.summer_project.R


class ProfileFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        val profileUpdate = view?.findViewById<TextView>(R.id.profileUpdate)
        if (profileUpdate != null) {
            profileUpdate.setOnClickListener { val Intent = Intent(activity, ProfileUpdate::class.java)
                startActivity(Intent)
            }
        }


        return view
    }


}