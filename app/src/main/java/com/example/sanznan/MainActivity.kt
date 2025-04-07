package com.example.sanznan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavController
import com.example.sanznan.ui.auth.AuthNavigation
import com.example.sanznan.ui.auth.AuthViewModel
import com.example.sanznan.ui.main.AdminMainMenuScreen
import com.example.sanznan.ui.main.MainMenuScreen
import com.example.sanznan.ui.profile.ProfileScreen
import com.example.sanznan.ui.services.ServicesScreen
import com.example.sanznan.ui.food.FoodServiceScreen
import com.example.sanznan.ui.food.BreakfastMenuScreen
import com.example.sanznan.ui.food.LunchMenuScreen
import com.example.sanznan.ui.food.DinnerMenuScreen
import com.example.sanznan.ui.events.EventsNavigation
import com.example.sanznan.ui.theme.SanZnanTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SanZnanTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    var isAuthenticated by remember { mutableStateOf(false) }
                    var userRole by remember { mutableStateOf<String?>(null) }

                    NavHost(
                        navController = navController,
                        startDestination = if (isAuthenticated) "main" else "auth"
                    ) {
                        composable("auth") {
                            AuthNavigation(
                                onAuthSuccess = { role ->
                                    isAuthenticated = true
                                    userRole = role
                                    navController.navigate("main") {
                                        popUpTo("auth") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("main") {
                            if (userRole == "admin") {
                                AdminMainMenuScreen(
                                    onChangeAccountClick = {
                                        isAuthenticated = false
                                        userRole = null
                                        navController.navigate("auth") {
                                            popUpTo("main") { inclusive = true }
                                        }
                                    }
                                )
                            } else {
                                MainMenuScreen(
                                    onPersonalAccountClick = {
                                        navController.navigate("profile")
                                    },
                                    onServicesClick = {
                                        navController.navigate("services")
                                    },
                                    onChangeAccountClick = {
                                        isAuthenticated = false
                                        userRole = null
                                        navController.navigate("auth") {
                                            popUpTo("main") { inclusive = true }
                                        }
                                    }
                                )
                            }
                        }

                        composable("profile") {
                            ProfileScreen(navController = navController)
                        }

                        composable("services") {
                            ServicesScreen(
                                navController = navController,
                                onFoodServiceClick = {
                                    navController.navigate("food_service")
                                },
                                onEventsServiceClick = {
                                    navController.navigate("events")
                                }
                            )
                        }

                        composable("food_service") {
                            FoodServiceScreen(navController)
                        }

                        composable("breakfast_menu") {
                            BreakfastMenuScreen(navController)
                        }

                        composable("lunch_menu") {
                            LunchMenuScreen(navController)
                        }

                        composable("dinner_menu") {
                            DinnerMenuScreen(navController)
                        }

                        composable("events") {
                            EventsNavigation(
                                onNavigateBack = {
                                    navController.navigateUp()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "auth",
        modifier = modifier
    ) {
        composable("auth") {
            AuthNavigation(
                onAuthSuccess = {
                    navController.navigate("main") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }
        composable("main") {
            MainMenuScreen(
                onPersonalAccountClick = {
                    navController.navigate("profile")
                },
                onServicesClick = {
                    navController.navigate("services")
                },
                onChangeAccountClick = {
                    navController.navigate("auth") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            )
        }
        composable("profile") {
            ProfileScreen(navController = navController)
        }
        composable("services") {
            ServicesScreen(
                navController = navController,
                onFoodServiceClick = {
                    navController.navigate("food_service")
                },
                onEventsServiceClick = {
                    navController.navigate("events")
                }
            )
        }
        composable("food_service") {
            FoodServiceScreen(navController)
        }
        composable("breakfast_menu") {
            BreakfastMenuScreen(navController)
        }
        composable("lunch_menu") {
            LunchMenuScreen(navController)
        }
        composable("dinner_menu") {
            DinnerMenuScreen(navController)
        }
        composable("events") {
            EventsNavigation(
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
    }
}