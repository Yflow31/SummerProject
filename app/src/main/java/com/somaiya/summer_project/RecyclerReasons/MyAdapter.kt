package com.somaiya.summer_project.RecyclerReasons

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Visibility
import com.google.android.material.chip.Chip
import com.google.firebase.firestore.FirebaseFirestore
import com.somaiya.summer_project.R
import com.somaiya.summer_project.applyform.Model.ApplyFormData
import com.somaiya.summer_project.utils.ApprovalConstant
import com.somaiya.summer_project.utils.ROLE

class MyAdapter(
    private val applyform: ArrayList<ApplyFormData>,
    private val listener: ApprovalListener
) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {


    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val email = itemView.findViewById<TextView>(R.id.emailrc)
        val nooftimeslate = itemView.findViewById<TextView>(R.id.nooftimeslaterc)
        val reason = itemView.findViewById<TextView>(R.id.reasonrc)
        val location = itemView.findViewById<TextView>(R.id.locationrc)
        val accept = itemView.findViewById<Button>(R.id.approval_status_accept)
        val reject = itemView.findViewById<Button>(R.id.approval_status_reject)
        val status = itemView.findViewById<Chip>(R.id.approval_status_label)
        val btnLayout = itemView.findViewById<LinearLayout>(R.id.approval_status_btn_layout)

        val dtimerc = itemView.findViewById<TextView>(R.id.dtimerc)
        val dtimerc1 = itemView.findViewById<TextView>(R.id.dtimerc1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_reason_card, parent, false)
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
        holder.dtimerc.text = applyformcurrent.currentdate
        holder.dtimerc1.text = applyformcurrent.currenttime


        when (applyformcurrent.approvalStatus) {
            ApprovalConstant.PENDING.name -> {
                Log.d("STATUS", "PENDING")
                //show the buttons
                if (applyformcurrent.role == ROLE.admin.name){
                    //Approving button visiblity
                    holder.btnLayout.visibility = View.VISIBLE
                }
                holder.status.visibility = View.VISIBLE
                holder.status.text = ApprovalConstant.PENDING.name
            }

            ApprovalConstant.ACCEPTED.name -> {
                //show accepted badge
                Log.d("STATUS", "ACCEPTED")
                holder.status.visibility = View.VISIBLE
                holder.status.text = ApprovalConstant.ACCEPTED.name
                holder.status.chipBackgroundColor = ColorStateList.valueOf(Color.parseColor("#90EE90"))
                holder.btnLayout.visibility = View.GONE

            }

            ApprovalConstant.REJECTED.name -> {
                //show rejected badge
                Log.d("STATUS", "REJECTED")
                holder.status.visibility = View.VISIBLE
                holder.status.text = ApprovalConstant.REJECTED.name
                holder.status.chipBackgroundColor = ColorStateList.valueOf(Color.parseColor("#ff8389"))
                holder.btnLayout.visibility = View.GONE
            }


        }

        holder.accept.setOnClickListener {
            listener.onApprovalResult(true, position, applyformcurrent.reasonId)
        }

        holder.reject.setOnClickListener {
            listener.onApprovalResult(false, position, applyformcurrent.reasonId)
        }

    }
}
