package com.example.accounting.models

data class Project (
    val id: Long? = null,
    val name: String,
    val cost: Int,
    val department: Department,
    val dateBeg: String?,
    val dateEnd: String?,
    val dateEndReal: String?
)