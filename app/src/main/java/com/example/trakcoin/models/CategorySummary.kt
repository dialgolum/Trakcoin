package com.example.trakcoin.models

data class CategorySummary(
    val category: String,
    val total: Double,
    val type: String  // Income or expense
)
