package com.example.accounting.models

data class DepartmentEmployer(
    val id: Long? = null,
    val department: Department,
    val employer: Employer
)