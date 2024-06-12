package com.somaiya.summer_project.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.somaiya.summer_project.R
import com.somaiya.summer_project.ApplyForForm
import com.somaiya.summer_project.RecyclerReasons.MyAdapter
import com.somaiya.summer_project.RecyclerReasons.Reasons
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {

    lateinit var recyclerviewreason: RecyclerView

    lateinit var reasonList: ArrayList<Reasons>

    lateinit var reasonAdapter: MyAdapter

    lateinit var db: FirebaseFirestore

    lateinit var auth: FirebaseAuth


    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerviewreason = view.findViewById(R.id.recyclerviewreason)
        recyclerviewreason.layoutManager = LinearLayoutManager(context)
        reasonList = arrayListOf()
        reasonAdapter = MyAdapter(reasonList)
        recyclerviewreason.adapter = reasonAdapter


        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser

        if (currentUser != null) {
            db.collection("users")
                .document(currentUser.uid)
                .collection("reasons")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val reason = document.toObject(Reasons::class.java)
                        reasonList.add(reason)
                    }
                    reasonAdapter.notifyDataSetChanged()
                }
        }



        val fab = view.findViewById<View>(R.id.fab)
        fab.setOnClickListener {
            val Intent = Intent(activity, ApplyForForm::class.java)
            startActivity(Intent)
        }

        return view
    }

}
