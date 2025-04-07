package com.example.sanznan.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class NotificationPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    var notificationsEnabled: Boolean
        get() = prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
        set(value) = prefs.edit { putBoolean(KEY_NOTIFICATIONS_ENABLED, value) }

    var morningNotificationsEnabled: Boolean
        get() = prefs.getBoolean(KEY_MORNING_NOTIFICATIONS, true)
        set(value) = prefs.edit { putBoolean(KEY_MORNING_NOTIFICATIONS, value) }

    var eveningNotificationsEnabled: Boolean
        get() = prefs.getBoolean(KEY_EVENING_NOTIFICATIONS, true)
        set(value) = prefs.edit { putBoolean(KEY_EVENING_NOTIFICATIONS, value) }

    var morningNotificationHour: Int
        get() = prefs.getInt(KEY_MORNING_HOUR, DEFAULT_MORNING_HOUR)
        set(value) = prefs.edit { putInt(KEY_MORNING_HOUR, value) }

    var morningNotificationMinute: Int
        get() = prefs.getInt(KEY_MORNING_MINUTE, DEFAULT_MORNING_MINUTE)
        set(value) = prefs.edit { putInt(KEY_MORNING_MINUTE, value) }

    var eveningNotificationHour: Int
        get() = prefs.getInt(KEY_EVENING_HOUR, DEFAULT_EVENING_HOUR)
        set(value) = prefs.edit { putInt(KEY_EVENING_HOUR, value) }

    var eveningNotificationMinute: Int
        get() = prefs.getInt(KEY_EVENING_MINUTE, DEFAULT_EVENING_MINUTE)
        set(value) = prefs.edit { putInt(KEY_EVENING_MINUTE, value) }

    companion object {
        private const val PREFS_NAME = "notification_preferences"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_MORNING_NOTIFICATIONS = "morning_notifications"
        private const val KEY_EVENING_NOTIFICATIONS = "evening_notifications"
        private const val KEY_MORNING_HOUR = "morning_hour"
        private const val KEY_MORNING_MINUTE = "morning_minute"
        private const val KEY_EVENING_HOUR = "evening_hour"
        private const val KEY_EVENING_MINUTE = "evening_minute"

        const val DEFAULT_MORNING_HOUR = 9
        const val DEFAULT_MORNING_MINUTE = 0
        const val DEFAULT_EVENING_HOUR = 20
        const val DEFAULT_EVENING_MINUTE = 0
    }
} 