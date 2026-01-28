package com.example.eazyfind

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eazyfind.ui.navigation.AppNavGraph
import com.example.eazyfind.ui.themes.EazyFindTheme
import com.example.eazyfind.viewmodel.RestaurantViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            EazyFindTheme {
                val restaurantViewModel: RestaurantViewModel = viewModel()
                AppNavGraph(viewModel = restaurantViewModel)
            }
        }
    }
}
