package com.example.trakcoin.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
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


class ReportFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var pieChart: PieChart
    private lateinit var typeToggle: RadioGroup

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_report, container, false)

        pieChart = view.findViewById(R.id.pieChart)
        recyclerView = view.findViewById(R.id.recyclerCategorySummary)
        typeToggle = view.findViewById(R.id.typeToggle)

        typeToggle.setOnCheckedChangeListener{ _, checkedId ->
            val selectedType = if (checkedId == R.id.radioIncome) "Income" else "Expense"
            loadCategorySummary(selectedType)
        }


        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        loadCategorySummary("Income")
        return view
    }

    private fun loadCategorySummary(type: String){
        val transactions = SharedPrefManager(requireContext()).getAllTransactions()

        // Filter: Only Expenses (you can change this to Income)
        val filtered = transactions.filter { it.type == type }
        

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


}