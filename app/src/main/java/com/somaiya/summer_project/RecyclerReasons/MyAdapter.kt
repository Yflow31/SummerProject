package com.somaiya.summer_project.RecyclerReasons

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.somaiya.summer_project.R

class MyAdapter(private val reasons: ArrayList<Reasons>): RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val email = itemView.findViewById<TextView>(R.id.emailrc)
        val nooftimeslate = itemView.findViewById<TextView>(R.id.nooftimeslaterc)
        val reason = itemView.findViewById<TextView>(R.id.reasonrc)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_reason_card, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
       return reasons.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentreason = reasons[position]
        holder.email.text = currentreason.userEmail
        holder.nooftimeslate.text = currentreason.timesLate
        holder.reason.text = currentreason.reason

    }

}