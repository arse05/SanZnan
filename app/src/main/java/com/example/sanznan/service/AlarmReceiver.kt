package com.example.sanznan.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationService = NotificationService(context)
        
        when (intent.action) {
            ACTION_MORNING_NOTIFICATION -> notificationService.showMorningNotification()
            ACTION_EVENING_NOTIFICATION -> notificationService.showEveningNotification()
        }
    }

    companion object {
        const val ACTION_MORNING_NOTIFICATION = "com.example.sanznan.MORNING_NOTIFICATION"
        const val ACTION_EVENING_NOTIFICATION = "com.example.sanznan.EVENING_NOTIFICATION"
    }
} 