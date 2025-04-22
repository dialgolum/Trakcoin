package com.example.trakcoin.models


data class Transaction(
    val id: Int,
    val title: String,
    val amount: Double,
    val category: String,
    val date: String,
    val type: String // "income" or "expense"
)
