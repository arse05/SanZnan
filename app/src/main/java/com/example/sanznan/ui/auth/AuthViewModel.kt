package com.example.sanznan.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState()
    data class Success(val role: String? = null) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

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
                auth.signInWithEmailAndPassword(email, password).await()
                _authState.value = AuthState.Success()
            } catch (exception: Exception) {
                val errorMessage = when (exception) {
                    is FirebaseAuthInvalidUserException,
                    is FirebaseAuthInvalidCredentialsException -> "Неверный email или пароль"
                    else -> "Ошибка входа. Попробуйте позже"
                }
                _authState.value = AuthState.Error(errorMessage)
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
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val user = authResult.user
                if (user != null) {
                    val role = determineRole(email)
                    val userData = hashMapOf(
                        "email" to email,
                        "role" to role,
                        "uid" to user.uid
                    )

                    db.collection("users").document(user.uid)
                        .set(userData)
                        .await()

                    _authState.value = AuthState.Success(role)
                } else {
                    _authState.value = AuthState.Success()
                }
            } catch (exception: Exception) {
                val errorMessage = when (exception) {
                    is FirebaseAuthWeakPasswordException -> "Слишком простой пароль"
                    is FirebaseAuthInvalidCredentialsException -> "Неверный формат email"
                    else -> "Ошибка регистрации. Возможно, email уже используется"
                }
                _authState.value = AuthState.Error(errorMessage)
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun determineRole(email: String): String {
        return when {
            email.endsWith("@admin.com") -> "admin"
            else -> "user"
        }
    }

    fun resetState() {
        _authState.value = AuthState.Initial
    }
}