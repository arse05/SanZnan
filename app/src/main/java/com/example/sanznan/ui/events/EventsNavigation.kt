package com.example.sanznan.ui.events

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

sealed class EventsScreen(val route: String) {
    object EventsList : EventsScreen("events_list")
    object EventDetails : EventsScreen("event_details/{eventId}") {
        fun createRoute(eventId: String) = "event_details/$eventId"
    }
}

@Composable
fun EventsNavigation(
    navController: NavHostController = rememberNavController(),
    onNavigateBack: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = EventsScreen.EventsList.route
    ) {
        composable(EventsScreen.EventsList.route) {
            EventsScreen(
                onNavigateBack = onNavigateBack,
                onEventClick = { eventId ->
                    try {
                        navController.navigate(EventsScreen.EventDetails.createRoute(eventId))
                    } catch (e: Exception) {
                        // Обработка ошибки навигации
                        e.printStackTrace()
                    }
                }
            )
        }
        
        composable(
            route = EventsScreen.EventDetails.route,
            arguments = listOf(
                navArgument("eventId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            EventDetailsScreen(
                eventId = eventId,
                onNavigateBack = {
                    try {
                        navController.popBackStack()
                    } catch (e: Exception) {
                        // Обработка ошибки навигации
                        e.printStackTrace()
                    }
                }
            )
        }
    }
} 