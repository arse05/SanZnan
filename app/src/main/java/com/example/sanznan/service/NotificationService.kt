package com.example.sanznan.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.sanznan.MainActivity
import com.example.sanznan.R

class NotificationService(private val context: Context) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val morningChannel = NotificationChannel(
                CHANNEL_ID_MORNING,
                "Утренние уведомления",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Канал для утренних уведомлений"
            }

            val eveningChannel = NotificationChannel(
                CHANNEL_ID_EVENING,
                "Вечерние уведомления",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Канал для вечерних уведомлений"
            }

            notificationManager.createNotificationChannels(listOf(morningChannel, eveningChannel))
        }
    }

    fun showMorningNotification() {
        showNotification(
            CHANNEL_ID_MORNING,
            NOTIFICATION_ID_MORNING,
            "Доброе утро!",
            "Пора начать новый день с изучения"
        )
    }

    fun showEveningNotification() {
        showNotification(
            CHANNEL_ID_EVENING,
            NOTIFICATION_ID_EVENING,
            "Добрый вечер!",
            "Не забудьте повторить пройденный материал"
        )
    }

    private fun showNotification(
        channelId: String,
        notificationId: Int,
        title: String,
        message: String
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.logo_znanie)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    companion object {
        const val CHANNEL_ID_MORNING = "morning_notifications"
        const val CHANNEL_ID_EVENING = "evening_notifications"
        const val NOTIFICATION_ID_MORNING = 1
        const val NOTIFICATION_ID_EVENING = 2
    }
} 