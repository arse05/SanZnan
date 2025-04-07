package com.example.sanznan.ui.profile

import android.app.TimePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.sanznan.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.ui.text.input.PasswordVisualTransformation
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = viewModel()
) {
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var newPassword by remember { mutableStateOf("") }
    
    var notificationsEnabled by remember { mutableStateOf(viewModel.notificationsEnabled) }
    var morningNotificationsEnabled by remember { mutableStateOf(viewModel.morningNotificationsEnabled) }
    var eveningNotificationsEnabled by remember { mutableStateOf(viewModel.eveningNotificationsEnabled) }
    
    var morningHour by remember { mutableStateOf(viewModel.morningNotificationHour) }
    var morningMinute by remember { mutableStateOf(viewModel.morningNotificationMinute) }
    var eveningHour by remember { mutableStateOf(viewModel.eveningNotificationHour) }
    var eveningMinute by remember { mutableStateOf(viewModel.eveningNotificationMinute) }
    
    val profileState by viewModel.profileState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(profileState) {
        when (profileState) {
            is ProfileState.Success -> {
                scope.launch {
                    snackbarHostState.showSnackbar("Пароль успешно изменен")
                }
                showChangePasswordDialog = false
                newPassword = ""
                viewModel.resetState()
            }
            is ProfileState.Error -> {
                scope.launch {
                    snackbarHostState.showSnackbar((profileState as ProfileState.Error).message)
                }
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Личный кабинет") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_znanie),
                contentDescription = "Логотип",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(vertical = 16.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Настройки уведомлений",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Уведомления")
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { enabled ->
                                notificationsEnabled = enabled
                                viewModel.updateNotificationSettings(
                                    enabled,
                                    morningNotificationsEnabled,
                                    eveningNotificationsEnabled,
                                    morningHour,
                                    morningMinute,
                                    eveningHour,
                                    eveningMinute
                                )
                            }
                        )
                    }

                    if (notificationsEnabled) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Утренние уведомления")
                                    if (morningNotificationsEnabled) {
                                        Text(
                                            text = String.format(
                                                "%02d:%02d",
                                                morningHour,
                                                morningMinute
                                            ),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (morningNotificationsEnabled) {
                                        Button(
                                            onClick = {
                                                TimePickerDialog(
                                                    context,
                                                    { _, hour, minute ->
                                                        morningHour = hour
                                                        morningMinute = minute
                                                        viewModel.updateNotificationSettings(
                                                            notificationsEnabled,
                                                            morningNotificationsEnabled,
                                                            eveningNotificationsEnabled,
                                                            hour,
                                                            minute,
                                                            eveningHour,
                                                            eveningMinute
                                                        )
                                                    },
                                                    morningHour,
                                                    morningMinute,
                                                    true
                                                ).show()
                                            }
                                        ) {
                                            Text("Изменить время")
                                        }
                                    }
                                    Switch(
                                        checked = morningNotificationsEnabled,
                                        onCheckedChange = { enabled ->
                                            morningNotificationsEnabled = enabled
                                            viewModel.updateNotificationSettings(
                                                notificationsEnabled,
                                                enabled,
                                                eveningNotificationsEnabled,
                                                morningHour,
                                                morningMinute,
                                                eveningHour,
                                                eveningMinute
                                            )
                                        }
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Вечерние уведомления")
                                    if (eveningNotificationsEnabled) {
                                        Text(
                                            text = String.format(
                                                "%02d:%02d",
                                                eveningHour,
                                                eveningMinute
                                            ),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (eveningNotificationsEnabled) {
                                        Button(
                                            onClick = {
                                                TimePickerDialog(
                                                    context,
                                                    { _, hour, minute ->
                                                        eveningHour = hour
                                                        eveningMinute = minute
                                                        viewModel.updateNotificationSettings(
                                                            notificationsEnabled,
                                                            morningNotificationsEnabled,
                                                            eveningNotificationsEnabled,
                                                            morningHour,
                                                            morningMinute,
                                                            hour,
                                                            minute
                                                        )
                                                    },
                                                    eveningHour,
                                                    eveningMinute,
                                                    true
                                                ).show()
                                            }
                                        ) {
                                            Text("Изменить время")
                                        }
                                    }
                                    Switch(
                                        checked = eveningNotificationsEnabled,
                                        onCheckedChange = { enabled ->
                                            eveningNotificationsEnabled = enabled
                                            viewModel.updateNotificationSettings(
                                                notificationsEnabled,
                                                morningNotificationsEnabled,
                                                enabled,
                                                morningHour,
                                                morningMinute,
                                                eveningHour,
                                                eveningMinute
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Button(
                onClick = { showChangePasswordDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Сменить пароль")
            }

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Техническая поддержка",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text("Email: support@sanznan.com")
                    Text("Телефон: +7 (XXX) XXX-XX-XX")
                }
            }
        }

        if (showChangePasswordDialog) {
            AlertDialog(
                onDismissRequest = { 
                    showChangePasswordDialog = false
                    newPassword = ""
                },
                title = { Text("Смена пароля") },
                text = {
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("Новый пароль") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.changePassword(newPassword)
                        },
                        enabled = newPassword.length >= 6
                    ) {
                        Text("Сохранить")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showChangePasswordDialog = false
                            newPassword = ""
                        }
                    ) {
                        Text("Отмена")
                    }
                }
            )
        }

        if (profileState is ProfileState.Loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
} 