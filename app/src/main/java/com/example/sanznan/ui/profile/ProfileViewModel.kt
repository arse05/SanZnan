package com.example.sanznan.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sanznan.data.auth.FirebaseAuthRepository
import com.example.sanznan.data.preferences.NotificationPreferences
import com.example.sanznan.service.AlarmScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ProfileState {
    object Initial : ProfileState()
    object Loading : ProfileState()
    object Success : ProfileState()
    data class Error(val message: String) : ProfileState()
}

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = FirebaseAuthRepository()
    private val notificationPreferences = NotificationPreferences(application)
    private val alarmScheduler = AlarmScheduler(application)
    
    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Initial)
    val profileState: StateFlow<ProfileState> = _profileState

    val notificationsEnabled: Boolean
        get() = notificationPreferences.notificationsEnabled

    val morningNotificationsEnabled: Boolean
        get() = notificationPreferences.morningNotificationsEnabled

    val eveningNotificationsEnabled: Boolean
        get() = notificationPreferences.eveningNotificationsEnabled

    val morningNotificationHour: Int
        get() = notificationPreferences.morningNotificationHour

    val morningNotificationMinute: Int
        get() = notificationPreferences.morningNotificationMinute

    val eveningNotificationHour: Int
        get() = notificationPreferences.eveningNotificationHour

    val eveningNotificationMinute: Int
        get() = notificationPreferences.eveningNotificationMinute

    fun changePassword(newPassword: String) {
        if (newPassword.length < 6) {
            _profileState.value = ProfileState.Error("Пароль должен содержать минимум 6 символов")
            return
        }

        _profileState.value = ProfileState.Loading
        viewModelScope.launch {
            try {
                val user = authRepository.currentUser
                if (user != null) {
                    user.updatePassword(newPassword)
                        .addOnSuccessListener {
                            _profileState.value = ProfileState.Success
                        }
                        .addOnFailureListener { exception ->
                            _profileState.value = ProfileState.Error(
                                exception.message ?: "Ошибка при смене пароля"
                            )
                        }
                } else {
                    _profileState.value = ProfileState.Error("Необходимо повторно войти в систему")
                }
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error(e.message ?: "Неизвестная ошибка")
            }
        }
    }

    fun updateNotificationSettings(
        enabled: Boolean,
        morningEnabled: Boolean,
        eveningEnabled: Boolean,
        morningHour: Int = morningNotificationHour,
        morningMinute: Int = morningNotificationMinute,
        eveningHour: Int = eveningNotificationHour,
        eveningMinute: Int = eveningNotificationMinute
    ) {
        notificationPreferences.notificationsEnabled = enabled
        notificationPreferences.morningNotificationsEnabled = morningEnabled
        notificationPreferences.eveningNotificationsEnabled = eveningEnabled
        notificationPreferences.morningNotificationHour = morningHour
        notificationPreferences.morningNotificationMinute = morningMinute
        notificationPreferences.eveningNotificationHour = eveningHour
        notificationPreferences.eveningNotificationMinute = eveningMinute

        if (enabled) {
            if (morningEnabled) {
                alarmScheduler.scheduleMorningAlarm(morningHour, morningMinute)
            } else {
                alarmScheduler.cancelMorningAlarm()
            }

            if (eveningEnabled) {
                alarmScheduler.scheduleEveningAlarm(eveningHour, eveningMinute)
            } else {
                alarmScheduler.cancelEveningAlarm()
            }
        } else {
            alarmScheduler.cancelMorningAlarm()
            alarmScheduler.cancelEveningAlarm()
        }
    }

    fun resetState() {
        _profileState.value = ProfileState.Initial
    }
} 