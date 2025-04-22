package com.example.trakcoin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trakcoin.R
import com.example.trakcoin.models.CategorySummary

class CategorySummaryAdapter(private val summaries: List<CategorySummary>) :
    RecyclerView.Adapter<CategorySummaryAdapter.SummaryViewHolder>(){

    class SummaryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textCategory: TextView = view.findViewById(R.id.textCategory)
        val textTotal: TextView = view.findViewById(R.id.textTotal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SummaryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_summary, parent, false)
        return SummaryViewHolder(view)
    }

    override fun onBindViewHolder(holder: SummaryViewHolder, position: Int) {
        val item = summaries[position]
        holder.textCategory.text = item.category
        holder.textTotal.text = "Rs. %.2f".format(item.total)
        holder.textTotal.setTextColor(
            if(item.type == "Income") 0xFF2E7D32.toInt() else 0xFFC62828.toInt()
        )
    }

    override fun getItemCount(): Int = summaries.size





}