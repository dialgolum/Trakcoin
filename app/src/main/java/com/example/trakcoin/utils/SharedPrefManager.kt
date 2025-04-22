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

    fun updateTransaction(updated: Transaction) {
        val list = getAllTransactions().toMutableList()
        val index = list.indexOfFirst { it.id == updated.id }
        if (index != -1) {
            list[index] = updated
            val json = gson.toJson(list)
            editor.putString("transaction_list", json)
            editor.apply()
        }
    }

    fun deleteTransaction(id: Int) {
        val list = getAllTransactions().toMutableList()
        val updatedList = list.filter { it.id != id }
        val json = gson.toJson(updatedList)
        editor.putString("transaction_list", json)
        editor.apply()
    }

    fun saveMonthluBudget(budget: Double) {
        editor.putFloat("monthly_budget", budget.toFloat())
        editor.apply()
    }

    fun getMonthlyBudget(): Double? {
        val value = prefs.getFloat("monthly_budget", -1f)
        return if (value == -1f) null else value.toDouble()
    }

    fun saveCurrency(symbol: String) {
        editor.putString("currency", symbol)
        editor.apply()
    }

    fun getCurrency(): String {
        return prefs.getString("currency", "LKR") ?: "LKR"
    }

    fun replaceAllTransactions(transactions: List<Transaction>) {
        val json = Gson().toJson(transactions)
        editor.putString("transaction_list", json)
        editor.apply()
    }

}