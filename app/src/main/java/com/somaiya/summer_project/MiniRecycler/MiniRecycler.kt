package com.somaiya.summer_project.MiniRecycler

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.somaiya.summer_project.R
import com.somaiya.summer_project.applyform.Model.ApplyFormData
import com.somaiya.summer_project.utils.ApprovalConstant
import com.somaiya.summer_project.utils.BadgeTextView
import com.somaiya.summer_project.utils.ROLE

class MiniRecyclerAdapter(private val dataList: List<ApplyFormData>) :
    RecyclerView.Adapter<MiniRecyclerAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvApprovalStatus: BadgeTextView = itemView.findViewById(R.id.item_request_badge)
        val tvDate: TextView = itemView.findViewById(R.id.item_request_date)
        val tvRequestId: TextView = itemView.findViewById(R.id.item_request_id)

        fun bind(data: ApplyFormData, position: Int) {
            tvRequestId.text = "Request #${position + 1}"
            tvApprovalStatus.text = data.approvalStatus
            tvDate.text = "${data.currentdate} ${data.currenttime}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dashboard_late_request, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position], position)

        when (dataList[position].approvalStatus) {

            ApprovalConstant.PENDING.name -> {
                holder.tvApprovalStatus.setCustomBackgroundColor(Color.parseColor("#FDECD8"))
                holder.tvApprovalStatus.setTextColor(Color.parseColor("#F7A23B"))
            }

            ApprovalConstant.ACCEPTED.name -> {
                holder.tvApprovalStatus.setCustomBackgroundColor(Color.parseColor("#CFF4E4"))
                holder.tvApprovalStatus.setTextColor(Color.parseColor("#00AC41"))

            }

            ApprovalConstant.REJECTED.name -> {


                holder.tvApprovalStatus.setCustomBackgroundColor(Color.parseColor("#FDDFDF"))
                holder.tvApprovalStatus.setTextColor(Color.parseColor("#F75D5F"))
            }


        }

    }

    override fun getItemCount(): Int = dataList.size
}