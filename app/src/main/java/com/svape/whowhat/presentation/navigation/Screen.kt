package com.svape.whowhat.presentation.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Workout : Screen("workout")
    data object Weight : Screen("weight")
    data object Supplement : Screen("supplement")
    data object SkinCare : Screen("skincare")
}