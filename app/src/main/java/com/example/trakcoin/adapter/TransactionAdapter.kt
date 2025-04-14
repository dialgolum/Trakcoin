package com.example.trakcoin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trakcoin.R
import com.example.trakcoin.models.Transaction

class TransactionAdapter(private val transactions: List<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.textTitle)
        val amount: TextView = view.findViewById(R.id.textAmount)
        val details: TextView = view.findViewById(R.id.textDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val item = transactions[position]
        holder.title.text = item.title
        holder.amount.text = "Rs. %.2f".format(item.amount)
        holder.amount.setTextColor(
            if (item.type == "Income") 0xFF2E7D32.toInt() else 0xFFC62828.toInt()
        )
        holder.details.text = "${item.category} • ${item.date}"
    }

    override fun getItemCount(): Int = transactions.size
}