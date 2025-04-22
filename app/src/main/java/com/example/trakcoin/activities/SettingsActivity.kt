package com.example.trakcoin.activities

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.trakcoin.R
import com.example.trakcoin.utils.SharedPrefManager

class SettingsActivity : AppCompatActivity() {

    private lateinit var budgetInput: EditText
    private lateinit var currencySpinner: Spinner
    private lateinit var saveBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        budgetInput = findViewById(R.id.editBudget)
        currencySpinner = findViewById(R.id.spinnerCurrency)
        saveBtn = findViewById(R.id.btnSaveSettings)

        val prefs = SharedPrefManager(this)
        budgetInput.setText(prefs.getMonthlyBudget()?.toString() ?: "")

        val currencies = listOf("LKR", "USD", "EUR", "GBP", "INR")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        currencySpinner.adapter = adapter

        val savedCurrency = prefs.getCurrency()
        val index = currencies.indexOf(savedCurrency)
        if (index >= 0) currencySpinner.setSelection(index)

        saveBtn.setOnClickListener {
            val budget = budgetInput.text.toString().toDoubleOrNull()
            val currency = currencySpinner.selectedItem.toString()

            if (budget == null || budget <= 0) {
                Toast.makeText(this, "Enter a valid budget", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            prefs.saveMonthluBudget(budget)
            prefs.saveCurrency(currency)

            Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show()
            finish()
        }

        val back2 = findViewById<ImageButton>(R.id.ibBack2)

        back2.setOnClickListener {
            finish()
        }
    }
}