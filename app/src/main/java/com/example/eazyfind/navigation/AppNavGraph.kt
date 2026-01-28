package com.example.eazyfind.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eazyfind.ui.screens.HomeScreen
import com.example.eazyfind.ui.screens.SplashScreen
import com.example.eazyfind.viewmodel.RestaurantViewModel

@Composable
fun AppNavGraph(viewModel: RestaurantViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen {
                navController.navigate("home") {
                    popUpTo("splash") { inclusive = true }
                }
            }
        }

        composable("home") {
            HomeScreen()
        }
    }
}
