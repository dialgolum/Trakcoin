package com.example.trakcoin.fragments

import android.graphics.Color
import android.icu.text.Transliterator.Position
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioGroup
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trakcoin.R
import com.example.trakcoin.adapter.CategorySummaryAdapter
import com.example.trakcoin.models.CategorySummary
import com.example.trakcoin.utils.SharedPrefManager
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class ReportFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var pieChart: PieChart
    private lateinit var typeToggle: RadioGroup

    private val months = listOf(
        "All", "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_report, container, false)
        val spinnerMonth = view.findViewById<Spinner>(R.id.spinnerMonth)
        val monthAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, months)
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMonth.adapter = monthAdapter

        // Set default to current month
        val calendar = Calendar.getInstance()
        spinnerMonth.setSelection(calendar.get(Calendar.MONTH) + 1) // +1 because "All" is index 0

        // On change, reload chart
        spinnerMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long){
                val selectedType = if (typeToggle.checkedRadioButtonId == R.id.radioIncome) "Income" else "Expense"
                loadCategorySummary(selectedType, selectedMonth = months[position])
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        pieChart = view.findViewById(R.id.pieChart)
        recyclerView = view.findViewById(R.id.recyclerCategorySummary)
        typeToggle = view.findViewById(R.id.typeToggle)

        typeToggle.setOnCheckedChangeListener{ _, checkedId ->
            val selectedType = if (checkedId == R.id.radioIncome) "Income" else "Expense"
            val selectedMonth = spinnerMonth.selectedItem.toString()
            loadCategorySummary(selectedType, selectedMonth)
        }


        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        loadCategorySummary("Income", "All") // Default load
        return view
    }

    private fun loadCategorySummary(type: String, selectedMonth: String){
        val transactions = SharedPrefManager(requireContext()).getAllTransactions()

        // Filter: Only Expenses (you can change this to Income)
        val filtered = transactions.filter { txn ->
            txn.type == type && (
                    selectedMonth == "All" || getMonthName(txn.date) == selectedMonth
                    )
        }
        

        // Group by category
        val grouped = filtered.groupBy { it.category }

        // Build summary list for RecyclerView
        val summaryList = grouped.map { (category, list) ->
            CategorySummary(
                category = category,
                total = list.sumOf { it.amount },
                type = type
            )
        }

        // Setup RecyclerView
        recyclerView.adapter = CategorySummaryAdapter(summaryList)

        // --- PieChart Setup ---

        val pieEntries = summaryList.map {
            PieEntry(it.total.toFloat(), it.category)
        }

        val dataset = PieDataSet(pieEntries, "$type Categories")
        dataset.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataset.valueTextSize = 14f
        dataset.valueTextColor = Color.WHITE

        val pieData = PieData(dataset)

        pieChart.data = pieData
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.centerText = type
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.animateY(1000)
        pieChart.invalidate()

    }

    private fun getMonthName(dateStr: String): String{
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdf.parse(dateStr)
            val cal = Calendar.getInstance()
            cal.time = date!!
            months[cal.get(Calendar.MONTH) + 1] // index matches months list
        } catch (e: Exception) {
            "Unknown"
        }
    }


}