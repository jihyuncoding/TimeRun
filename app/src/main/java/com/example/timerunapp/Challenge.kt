package com.example.timerunapp

import kotlin.time.Duration

data class Challenge(
    val id: Int,
    val name: String,
    val goal: String,
    val category: String,
    val startDate: String,
    val duration: Int,
    val isCompleted: Int,
    val dDay: String,
    val endDate: String,
    var isChecked: Boolean = false // 체크 상태 추가
)