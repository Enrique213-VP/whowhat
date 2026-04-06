package com.svape.whowhat.presentation.navigation

sealed class Screen(val route: String) {
    data object Workout : Screen("workout")
    data object Weight : Screen("weight")
    data object Supplement : Screen("supplement")
    data object SkinCare : Screen("skincare")

    data object AddExercise : Screen("add_exercise")
    data object ExerciseList : Screen("exercise_list")
    data object WorkoutDetail : Screen("workout_detail/{sessionId}") {
        fun createRoute(sessionId: Long) = "workout_detail/$sessionId"
    }
}