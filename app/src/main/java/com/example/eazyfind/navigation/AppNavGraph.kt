package com.example.eazyfind.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eazyfind.ui.screens.HomeScreen
import com.example.eazyfind.ui.screens.SplashScreen
import com.example.eazyfind.viewmodel.RestaurantViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import java.net.URLDecoder
import java.net.URLEncoder
import com.example.eazyfind.ui.screens.WebViewScreen


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
            HomeScreen(
                onRestaurantClick = { url ->
                    val encodedUrl = URLEncoder.encode(url, "UTF-8")
                    navController.navigate("webview/$encodedUrl")
                }
            )
        }

        composable(
            route = "webview/{url}",
            arguments = listOf(
                navArgument("url") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val encodedUrl = backStackEntry.arguments?.getString("url") ?: return@composable
            val decodedUrl = URLDecoder.decode(encodedUrl, "UTF-8")

            WebViewScreen(url = decodedUrl)
        }
    }
}
