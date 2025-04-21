package com.example.trakcoin.fragments

import android.app.NotificationManager
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.example.trakcoin.R
import com.example.trakcoin.utils.NotificationUtils
import com.example.trakcoin.utils.SharedPrefManager
import java.text.SimpleDateFormat
import java.util.Locale


class HomeFragment : Fragment() {

    private lateinit var budgetStatus: TextView
    private lateinit var budgetProgress: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        budgetStatus = view.findViewById(R.id.textBudgetStatus)
        budgetProgress = view.findViewById(R.id.budgetProgress)

        showBudgetProgress()

        return view
    }

    override fun onResume() {
        super.onResume()
        showBudgetProgress()
    }

    private fun showBudgetProgress() {
        val pref = SharedPrefManager(requireContext())
        val budget = pref.getMonthlyBudget() ?: 0.0
        val currency = pref.getCurrency()

        val allTransactions = pref.getAllTransactions()
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val totalSpent = allTransactions
            .filter { it.type == "Expense" }
            .filter {
                try {
                    val date = sdf.parse(it.date)
                    val cal = Calendar.getInstance().apply { time = date!! }
                    cal.get(Calendar.MONTH) + 1 == currentMonth &&
                            cal.get(Calendar.YEAR) == currentYear
                } catch (e: Exception) {
                    false
                }
            }
            .sumOf { it.amount }

        val percent = if (budget > 0) (totalSpent / budget * 100).toInt().coerceAtMost(100) else 0

        budgetStatus.text = "Budget Used: $currency %.2f / %.2f".format(totalSpent, budget)

        budgetProgress.progress = 0
        budgetProgress.progress = percent

        // ✅ Set color based on budget usage

        val color = when {
            percent >= 100 -> Color.RED
            percent >= 80 -> Color.parseColor("#FF9800")
            else -> Color.parseColor("#4CAF50")
        }

        budgetProgress.progressTintList = ColorStateList.valueOf(color)

        if (percent >= 100) {
            sendBudgetNotification("🚨 Budget Exceeded", "You've gone over your budget!")
        } else if (percent >= 80) {
            sendBudgetNotification("⚠️ Budget Warning", "You're over 80% of your budget!")
        }


    }

    private fun sendBudgetNotification(title: String, message: String) {
        val notificationManager =
            requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(requireContext(), NotificationUtils.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(1, notification)
    }

}