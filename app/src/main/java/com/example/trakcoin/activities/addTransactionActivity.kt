package com.example.trakcoin.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.trakcoin.R
import com.example.trakcoin.models.Transaction
import com.example.trakcoin.utils.SharedPrefManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import android.util.Log
import com.google.gson.Gson


class addTransactionActivity : AppCompatActivity() {

    private lateinit var titleInput: EditText
    private lateinit var amountInput: EditText
    private lateinit var categoryInput: Spinner
    private lateinit var dateInput: EditText
    private lateinit var typeGroup: RadioGroup
    private lateinit var saveBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_transaction)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        titleInput = findViewById(R.id.editTitle)
        amountInput = findViewById(R.id.editAmount)
        categoryInput = findViewById(R.id.spinnerCategory)
        dateInput = findViewById(R.id.editDate)
        typeGroup = findViewById(R.id.radioType)
        saveBtn = findViewById(R.id.btnSave)


        val dateInput = findViewById<EditText>(R.id.editDate)
        val calendar = Calendar.getInstance()

        val editJson = intent.getStringExtra("transactionToEdit")
        val editingTransaction = editJson?.let { Gson().fromJson(it, Transaction::class.java) }

        if (editingTransaction != null) {
            titleInput.setText(editingTransaction.title)
            amountInput.setText(editingTransaction.amount.toString())
            dateInput.setText(editingTransaction.date)

            // Set spinner
            val categoryArray = resources.getStringArray(R.array.categories)
            val catIndex = categoryArray.indexOf(editingTransaction.category)
            if (catIndex >= 0) categoryInput.setSelection(catIndex)

            // Set radio button
            val typeId = if (editingTransaction.type == "Income") R.id.radioIncome else R.id.radioExpense
            typeGroup.check(typeId)

            saveBtn.text = "Update Transaction"
        }

        // Set default date to today
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        dateInput.setText(dateFormat.format(calendar.time))

        dateInput.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)
                dateInput.setText(dateFormat.format(selectedDate.time))
            }, year, month, day)

            // 🛑 Disable future dates
            datePicker.datePicker.maxDate = System.currentTimeMillis()

            datePicker.show()
        }



        val spinner = findViewById<Spinner>(R.id.spinnerCategory)
        ArrayAdapter.createFromResource(
            this, R.array.categories, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        // Save button click
        saveBtn.setOnClickListener {

            val title = titleInput.text.toString()
            val amount = amountInput.text.toString().toDoubleOrNull()
            val category = categoryInput.selectedItem.toString()
            val date = dateInput.text.toString()
            val typeId = typeGroup.checkedRadioButtonId
            val type = findViewById<RadioButton>(typeId).text.toString()


            if (title.isEmpty() || amount == null || typeId == -1) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save to SharedPreferences
            val newtransaction = Transaction(
                id = editingTransaction?.id ?: (0..99999).random(), // Random ID for simplicity
                title = title,
                amount = amount,
                category = category,
                date = date,
                type = type
            )


            val prefManager = SharedPrefManager(this)

            if (editingTransaction != null) {
                prefManager.updateTransaction(newtransaction)
                Toast.makeText(this, "Transaction updated", Toast.LENGTH_SHORT).show()
            } else {
                prefManager.saveTransaction(newtransaction)
                Toast.makeText(this, "Transaction saved", Toast.LENGTH_SHORT).show()
            }

            finish()
        }

    }
}