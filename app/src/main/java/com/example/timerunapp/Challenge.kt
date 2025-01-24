package com.example.timerunapp

data class Challenge(
    val id: Int,
    val name: String,
    val goal: String,
    val category: String,
    val startDate: String,
    val endDate: String
)