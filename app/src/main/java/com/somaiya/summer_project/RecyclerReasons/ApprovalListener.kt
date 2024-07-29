package com.somaiya.summer_project.RecyclerReasons

interface ApprovalListener {
    fun onApprovalResult(isApproved: Boolean,position: Int, reasonId: String)
}