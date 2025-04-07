package com.example.sanznan.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sanznan.data.auth.FirebaseAuthRepository
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val authRepository = FirebaseAuthRepository()
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState

    fun signIn(email: String, password: String) {
        if (!isValidEmail(email)) {
            _authState.value = AuthState.Error("Неверный формат email")
            return
        }

        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val result = authRepository.signIn(email, password)
                result.fold(
                    onSuccess = {
                        _authState.value = AuthState.Success
                    },
                    onFailure = { exception ->
                        val errorMessage = when (exception) {
                            is FirebaseAuthInvalidUserException,
                            is FirebaseAuthInvalidCredentialsException -> "Неверный email или пароль"
                            else -> "Ошибка входа. Попробуйте позже"
                        }
                        _authState.value = AuthState.Error(errorMessage)
                    }
                )
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Ошибка входа. Попробуйте позже")
            }
        }
    }

    fun signUp(email: String, password: String) {
        if (!isValidEmail(email)) {
            _authState.value = AuthState.Error("Неверный формат email")
            return
        }

        if (password.length < 6) {
            _authState.value = AuthState.Error("Пароль должен содержать минимум 6 символов")
            return
        }

        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val result = authRepository.signUp(email, password)
                result.fold(
                    onSuccess = {
                        _authState.value = AuthState.Success
                    },
                    onFailure = { exception ->
                        val errorMessage = when (exception) {
                            is FirebaseAuthWeakPasswordException -> "Слишком простой пароль"
                            is FirebaseAuthInvalidCredentialsException -> "Неверный формат email"
                            else -> "Ошибка регистрации. Возможно, email уже используется"
                        }
                        _authState.value = AuthState.Error(errorMessage)
                    }
                )
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Ошибка регистрации. Попробуйте позже")
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun resetState() {
        _authState.value = AuthState.Initial
    }
} 