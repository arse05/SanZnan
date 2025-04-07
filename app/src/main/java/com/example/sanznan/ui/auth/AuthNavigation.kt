package com.example.sanznan.ui.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

sealed class AuthScreen(val route: String) {
    object Login : AuthScreen("login")
    object Register : AuthScreen("register")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthNavigation(
    onAuthSuccess: () -> Unit
) {
    val navController = rememberNavController()
    val viewModel: AuthViewModel = viewModel()
    val authState by viewModel.authState.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            NavHost(
                navController = navController,
                startDestination = AuthScreen.Login.route
            ) {
                composable(AuthScreen.Login.route) {
                    LoginScreen(
                        onLoginClick = { email, password ->
                            viewModel.signIn(email, password)
                        },
                        onRegisterClick = {
                            navController.navigate(AuthScreen.Register.route)
                        }
                    )
                }

                composable(AuthScreen.Register.route) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = { Text("Регистрация") },
                                navigationIcon = {
                                    IconButton(onClick = { navController.navigateUp() }) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Назад"
                                        )
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            RegisterScreen(
                                onRegisterClick = { email, password ->
                                    viewModel.signUp(email, password)
                                },
                                onBackToLoginClick = {
                                    navController.navigateUp()
                                }
                            )
                        }
                    }
                }
            }

            // Показываем индикатор загрузки при необходимости
            if (authState is AuthState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            LaunchedEffect(authState) {
                when (authState) {
                    is AuthState.Success -> {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = if (navController.currentBackStackEntry?.destination?.route == AuthScreen.Register.route) {
                                    "Регистрация успешно завершена"
                                } else {
                                    "Вход выполнен успешно"
                                }
                            )
                        }
                        // Небольшая задержка, чтобы пользователь успел увидеть сообщение
                        kotlinx.coroutines.delay(1000)
                        onAuthSuccess()
                        viewModel.resetState()
                    }
                    is AuthState.Error -> {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = (authState as AuthState.Error).message
                            )
                        }
                        viewModel.resetState()
                    }
                    else -> {}
                }
            }
        }
    }
} 