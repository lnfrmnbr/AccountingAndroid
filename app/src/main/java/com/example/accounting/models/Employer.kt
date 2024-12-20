package com.example.accounting.models

data class Employer (
    val id: Long? = null,
    val firstName: String,
    val fatherName: String?,
    val lastName: String,
    val position: String,
    val salary: Int
)