package com.svape.whowhat.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.svape.whowhat.presentation.components.BottomNavBar
import com.svape.whowhat.presentation.screens.skincare.SkinCareScreen
import com.svape.whowhat.presentation.screens.supplement.SupplementScreen
import com.svape.whowhat.presentation.screens.weight.WeightScreen
import com.svape.whowhat.presentation.screens.workout.WorkoutScreen

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController()
) {
    Scaffold(
        bottomBar = { BottomNavBar(navController = navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Workout.route,
            modifier = Modifier.padding(innerPadding)
        ) {
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