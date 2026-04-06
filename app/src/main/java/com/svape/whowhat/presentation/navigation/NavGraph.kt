package com.svape.whowhat.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.svape.whowhat.presentation.components.BottomNavBar
import com.svape.whowhat.presentation.screens.skincare.SkinCareScreen
import com.svape.whowhat.presentation.screens.splash.SplashScreen
import com.svape.whowhat.presentation.screens.supplement.SupplementScreen
import com.svape.whowhat.presentation.screens.weight.WeightScreen
import com.svape.whowhat.presentation.screens.workout.WorkoutScreen

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute != Screen.Splash.route

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    onSplashFinished = {
                        navController.navigate(Screen.Workout.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Workout.route) {
                WorkoutScreen(navController = navController)
            }
            composable(Screen.Weight.route) {
                WeightScreen()
            }
            composable(Screen.Supplement.route) {
                SupplementScreen()
            }
            composable(Screen.SkinCare.route) {
                SkinCareScreen()
            }
        }
    }
}