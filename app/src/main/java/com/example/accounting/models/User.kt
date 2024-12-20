package com.example.accounting.models

data class User (
    val login: String,
    val password: String,
    val admin: Boolean
)