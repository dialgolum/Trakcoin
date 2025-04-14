package com.example.trakcoin.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trakcoin.R
import com.example.trakcoin.activities.addTransactionActivity
import com.example.trakcoin.adapter.TransactionAdapter
import com.example.trakcoin.utils.SharedPrefManager
import com.google.android.material.floatingactionbutton.FloatingActionButton

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
        adapter = TransactionAdapter(transactions)
        recyclerView.adapter = adapter
    }
}