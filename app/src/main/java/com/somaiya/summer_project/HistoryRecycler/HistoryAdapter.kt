package com.somaiya.summer_project.HistoryRecycler

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class HistoryAdapter(private val HistoryList : ArrayList<History>): RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryAdapter.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: HistoryAdapter.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return HistoryList.size
    }
}