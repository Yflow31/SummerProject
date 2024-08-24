package com.somaiya.summer_project.applyform.Model

import com.somaiya.summer_project.utils.ApprovalConstant
import com.somaiya.summer_project.utils.ROLE

data class ApplyFormData(
    val reasonForBeingLate: String = "", // Provide default values
    val location: String = "",
    var timesLate: String = "",
    val email: String = "",
    var userId: String = "",
    var reasonId: String = "",
    var approvalStatus: String = "",
    var role: String = "",
    val currentdate: String = "",
    val currenttime: String = "",
    val subject: String = "",
    val faculty: String = "",
    val selectedTimeSlot: String = ""
) {
    init {
        approvalStatus = approvalStatus.ifEmpty { ApprovalConstant.PENDING.name }
        role = role.ifEmpty { ROLE.student.name }
    }

}
