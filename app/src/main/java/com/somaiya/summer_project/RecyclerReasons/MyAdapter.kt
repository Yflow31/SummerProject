package com.somaiya.summer_project.RecyclerReasons

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.somaiya.summer_project.R
import com.somaiya.summer_project.applyform.Model.ApplyFormData

class MyAdapter(private val applyform: ArrayList<ApplyFormData>, private val listener: ApprovalListener): RecyclerView.Adapter<MyAdapter.MyViewHolder>() {


    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()


    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val email = itemView.findViewById<TextView>(R.id.emailrc)
        val nooftimeslate = itemView.findViewById<TextView>(R.id.nooftimeslaterc)
        val reason = itemView.findViewById<TextView>(R.id.reasonrc)
        val location = itemView.findViewById<TextView>(R.id.locationrc)
        val checkbox = itemView.findViewById<CheckBox>(R.id.checkbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_reason_card, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return applyform.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val applyformcurrent = applyform[position]
        holder.email.text = applyformcurrent.email
        holder.nooftimeslate.text = applyformcurrent.timesLate
        holder.reason.text = applyformcurrent.reasonForBeingLate
        holder.location.text = applyformcurrent.location

        // Set the initial checked state of the checkbox
        holder.checkbox.isChecked = applyformcurrent.isCheckboxChecked

        // Handle the checkbox state change
        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                listener.onApprovalResult(true,position,applyformcurrent.reasonId)
            } else {
                Log.d("ischecked", "false")
            }
        }
    }
}
