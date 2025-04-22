package com.example.trakcoin.receivers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.trakcoin.R
import com.example.trakcoin.utils.NotificationUtils

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, NotificationUtils.CHANNEL_ID)
            .setSmallIcon(R.drawable.trakoin)
            .setContentTitle("💰 Don’t forget to track expenses!")
            .setContentText("Log your daily spending in TrakCoin 📲")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        manager.notify(2, notification)
    }

}