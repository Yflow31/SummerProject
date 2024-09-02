package com.somaiya.summer_project.MiniRecycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.somaiya.summer_project.R
import com.somaiya.summer_project.applyform.Model.ApplyFormData

class LeaderboardRecyclerAdapter(
    private val applyFormDataList: List<ApplyFormData>
) : RecyclerView.Adapter<LeaderboardRecyclerAdapter.LeaderboardViewHolder>() {

    // ViewHolder class to hold the views for each item
    class LeaderboardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val emailTextView: TextView = itemView.findViewById(R.id.email_text)
        val count: TextView = itemView.findViewById(R.id.item_count)
        val approval: TextView = itemView.findViewById(R.id.item_request_badge)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dashboard_late_request, parent, false)
        return LeaderboardViewHolder(itemView)
    }

    // Bind the data to the views
    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        val currentItem = applyFormDataList[position]
        holder.emailTextView.text = currentItem.email

    }

    // Return the size of your dataset
    override fun getItemCount(): Int {
        return applyFormDataList.size
    }
}