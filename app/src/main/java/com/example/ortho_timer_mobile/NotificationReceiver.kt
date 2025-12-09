package com.example.ortho_timer_mobile

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            "START_PAUSE_ACTION" -> {
                val actionIntent = Intent("ACTION_START_PAUSE")
                context.sendBroadcast(actionIntent)
            }
            "LUNCH_ACTION" -> {
                val actionIntent = Intent("ACTION_LUNCH")
                context.sendBroadcast(actionIntent)
            }
            "FINISH_DAY_ACTION" -> {
                val actionIntent = Intent("ACTION_FINISH_DAY")
                context.sendBroadcast(actionIntent)
            }
        }
    }
}