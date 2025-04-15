package com.example.trakcoin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trakcoin.R
import com.example.trakcoin.models.Transaction

class TransactionAdapter(private val transactions: MutableList<Transaction>,
                         private val onItemClick: (Transaction) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.textTitle)
        val amount: TextView = view.findViewById(R.id.textAmount)
        val details: TextView = view.findViewById(R.id.textDetails)
        val icon: ImageView = view.findViewById(R.id.iconType)
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
        holder.details.text = "${item.category} • ${item.date}"

        if (item.type == "Income") {
            holder.amount.setTextColor(0xFF2E7D32.toInt()) //Green
            holder.icon.setImageResource(R.drawable.ic_income)
            holder.icon.setColorFilter(0xFF2E7D32.toInt())
        } else {
            holder.amount.setTextColor(0xFFC62828.toInt()) //Red
            holder.icon.setImageResource(R.drawable.ic_expense)
            holder.icon.setColorFilter(0xFFC62828.toInt())
        }

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }

    }

    override fun getItemCount(): Int = transactions.size

    fun removeItem(position: Int) {
        transactions.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getTransactionAt(position: Int): Transaction = transactions[position]
}