package com.example.trakcoin.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.trakcoin.models.Transaction
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPrefManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("transactions_prefs", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = prefs.edit()
    private val gson = Gson()

    fun saveTransaction(transaction: Transaction) {
        val transactions = getAllTransactions().toMutableList()
        transactions.add(transaction)
        val json = gson.toJson(transactions)
        editor.putString("transaction_list", json)
        editor.apply()
    }

    fun getAllTransactions(): List<Transaction> {
        val json = prefs.getString("transaction_list", null) ?: return emptyList()
        val type = object : TypeToken<List<Transaction>>() {}.type
        return gson.fromJson(json, type)
    }
}