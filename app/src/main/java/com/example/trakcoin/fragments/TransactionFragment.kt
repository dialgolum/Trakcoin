package com.example.trakcoin.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.trakcoin.R
import com.example.trakcoin.activities.addTransactionActivity
import com.example.trakcoin.adapter.TransactionAdapter
import com.example.trakcoin.utils.SharedPrefManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson

class TransactionFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: FloatingActionButton
    private lateinit var adapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_transaction, container, false)

        recyclerView = view.findViewById(R.id.recyclerTransactions)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        fab = view.findViewById(R.id.fabAdd)
        fab.setOnClickListener {
            startActivity(Intent(activity, addTransactionActivity::class.java))
        }

        return view
    }

    override fun onResume() { // reload data after returning from AddTransactionActivity
        super.onResume()
        loadTransactions()
    }

    private fun loadTransactions() {
        val prefManager = SharedPrefManager(requireContext())
        val transactions = prefManager.getAllTransactions()

            val adapter = TransactionAdapter(transactions.toMutableList()) { selectedTransaction ->
                val intent = Intent(requireContext(), addTransactionActivity::class.java)
                intent.putExtra("transactionToEdit", Gson().toJson(selectedTransaction))
                startActivity(intent)
            }

        recyclerView.adapter = adapter

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val transaction = adapter.getTransactionAt(position)

                AlertDialog.Builder(requireContext())
                    .setTitle("Delete Transaction")
                    .setMessage("Are you sure you want to delete \"${transaction.title}\"?")
                    .setPositiveButton("Delete") { _, _ ->
                        SharedPrefManager(requireContext()).deleteTransaction(transaction.id)  // Remove from list & notify adapter
                        adapter.removeItem(position)  // Remove from list & notify adapter
                        Toast.makeText(requireContext(), "Transaction deleted", Toast.LENGTH_SHORT).show()
                    }

                    .setNegativeButton("Cancel") { _, _ ->
                        adapter.notifyItemChanged(position) // Restore the swiped item
                    }
                    .setCancelable(false)
                    .show()

            }
        })

        itemTouchHelper.attachToRecyclerView(recyclerView)
    }
}