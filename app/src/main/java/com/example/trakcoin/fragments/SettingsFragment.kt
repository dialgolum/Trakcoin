package com.example.trakcoin.fragments

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.trakcoin.R
import com.example.trakcoin.activities.SettingsActivity
import com.example.trakcoin.models.Transaction
import com.example.trakcoin.utils.NotificationUtils
import com.example.trakcoin.utils.SharedPrefManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class SettingsFragment : Fragment() {

    private val fileName = "backup_transactions.json"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_settings, container, false)

        val btnSettings = view.findViewById<Button>(R.id.btnOpenSettings)
        btnSettings.setOnClickListener {
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.btnExportData).setOnClickListener {
            exportTransactionData()
        }

        view.findViewById<Button>(R.id.btnRestoreData).setOnClickListener {
            restoreTransactionData()
        }

        view.findViewById<Button>(R.id.btnExportAsText).setOnClickListener {
            exportDataAsText()
        }

        view.findViewById<Button>(R.id.btnSetReminderTime).setOnClickListener {
            showTimePicker()
        }


        return view
    }

    private fun exportTransactionData() {
        val transactions = SharedPrefManager(requireContext()).getAllTransactions()
        val json = Gson().toJson(transactions)

        try {
            val file = File(requireContext().filesDir,fileName)
            file.writeText(json)
            Toast.makeText(requireContext(), "Data exported to internal storage", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun restoreTransactionData() {
        try {
            val file = File(requireContext().filesDir,fileName)
            if (!file.exists()) {
                Toast.makeText(requireContext(), "No backup found", Toast.LENGTH_SHORT).show()
                return
            }

            val json = file.readText()
            val type = object : TypeToken<List<Transaction>>() {}.type
            val restoredList: List<Transaction> = Gson().fromJson(json, type)

//            Log.d("RESTORE_DEBUG", "Restored list size: ${restoredList.size}")

            SharedPrefManager(requireContext()).replaceAllTransactions(restoredList)

            Toast.makeText(requireContext(), "Data restored from backup", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Restore failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun exportDataAsText() {
        val transactions = SharedPrefManager(requireContext()).getAllTransactions()

        if (transactions.isEmpty()) {
            Toast.makeText(requireContext(), "No transactions to export", Toast.LENGTH_SHORT).show()
            return
        }

        val fileName = "transactions_report.txt"
        val file = File(requireContext().filesDir, fileName)

        try {
            val builder = StringBuilder()
            builder.append("TrakCoin - Transaction Report\n")
            builder.append("=============================\n\n")

            transactions.forEachIndexed { index, txn ->
                builder.append("${index + 1}. ${txn.title}\n")
                builder.append("    Type: ${txn.type}\n")
                builder.append("    Amount: ${txn.amount}\n")
                builder.append("    Category: ${txn.category}\n")
                builder.append("    Date: ${txn.date}\n")
                builder.append("\n")
            }

            builder.append("Total transactions: ${transactions.size}\n")
            file.writeText(builder.toString())

            Log.d("EXPORT_TXT", "Written to: ${file.absolutePath}")
            Log.d("EXPORT_TXT", "File exists: ${file.exists()}")
            Log.d("EXPORT_TXT", "Size: ${file.length()} bytes")
            Log.d("EXPORT_TXT", "Content:\n${file.readText()}")

            Toast.makeText(requireContext(), "Data exported as a text file.", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Export failed: ${e.message}", Toast.LENGTH_LONG).show()
            Log.e("EXPORT_TXT", "Export error: ${e.message}")
        }
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePicker  = TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
            saveReminderTime(selectedHour, selectedMinute)
            NotificationUtils.scheduleDailyReminder(requireContext(), selectedHour, selectedMinute)

            Toast.makeText(
                requireContext(),
                "Reminder set for %02d:%02d".format(selectedHour, selectedMinute),
                Toast.LENGTH_SHORT
            ).show()
        }, hour, minute, true)
        timePicker.show()
    }

    private fun saveReminderTime(hour: Int, minute: Int) {
        val prefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
        prefs.edit()
            .putInt("reminder_hour", hour)
            .putInt("reminder_minute", minute)
            .apply()
    }

}