package com.somaiya.summer_project.applyform.Model

import com.somaiya.summer_project.utils.ApprovalConstant

data class ApplyFormData(
    val reasonForBeingLate: String = "", // Provide default values
    val location: String = "",
    val timesLate: String = "",
    val email: String = "",
    val userId: String = "",
    var isCheckboxChecked: Boolean = false,
    var reasonId: String = "",
    var approvalStatus: String = ""
) {
    init {
        approvalStatus = approvalStatus.ifEmpty { ApprovalConstant.PENDING.name }
    }
}
