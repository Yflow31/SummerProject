package com.somaiya.summer_project.RecyclerReasons

interface ApprovalListener {
    fun onApprovalResult(isApproved: Boolean,position: Int, reasonId: String)

    // Add more methods as needed
    fun onDeleteResult(position: Int,reasonId: String)
}