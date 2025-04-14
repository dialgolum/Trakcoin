package com.example.trakcoin.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.trakcoin.R
import com.example.trakcoin.activities.addTransactionActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TransactionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_transaction, container, false)

        val fabAdd = view.findViewById<FloatingActionButton>(R.id.fabAdd)
        fabAdd.setOnClickListener {
            val intent = Intent(activity, addTransactionActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}