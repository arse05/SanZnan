package com.example.sanznan.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

class AlarmScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleMorningAlarm(hour: Int, minute: Int) {
        scheduleAlarm(
            hour,
            minute,
            MORNING_ALARM_REQUEST_CODE,
            AlarmReceiver.ACTION_MORNING_NOTIFICATION
        )
    }

    fun scheduleEveningAlarm(hour: Int, minute: Int) {
        scheduleAlarm(
            hour,
            minute,
            EVENING_ALARM_REQUEST_CODE,
            AlarmReceiver.ACTION_EVENING_NOTIFICATION
        )
    }

    private fun scheduleAlarm(
        hour: Int,
        minute: Int,
        requestCode: Int,
        action: String
    ) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            this.action = action
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            
            // Если время уже прошло, переносим на следующий день
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    fun cancelMorningAlarm() {
        cancelAlarm(MORNING_ALARM_REQUEST_CODE, AlarmReceiver.ACTION_MORNING_NOTIFICATION)
    }

    fun cancelEveningAlarm() {
        cancelAlarm(EVENING_ALARM_REQUEST_CODE, AlarmReceiver.ACTION_EVENING_NOTIFICATION)
    }

    private fun cancelAlarm(requestCode: Int, action: String) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            this.action = action
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    companion object {
        private const val MORNING_ALARM_REQUEST_CODE = 100
        private const val EVENING_ALARM_REQUEST_CODE = 101
    }
} 