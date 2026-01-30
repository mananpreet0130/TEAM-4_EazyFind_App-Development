package com.example.eazyfind

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eazyfind.ui.navigation.AppNavGraph
import com.example.eazyfind.ui.themes.EazyFindTheme
import com.example.eazyfind.viewmodel.LocationViewModel
import com.example.eazyfind.viewmodel.RestaurantViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            EazyFindTheme {

                val context = LocalContext.current

                // ✅ SINGLE instances (shared everywhere)
                val restaurantViewModel: RestaurantViewModel = viewModel()
                val locationViewModel: LocationViewModel = viewModel()

                var permissionRequested by remember { mutableStateOf(false) }

                val locationPermissionLauncher =
                    rememberLauncherForActivityResult(
                        ActivityResultContracts.RequestPermission()
                    ) { isGranted ->
                        if (isGranted) {
                            restaurantViewModel.detectCityFromLocation(context)
                        }
                    }

                LaunchedEffect(Unit) {
                    if (!permissionRequested) {
                        permissionRequested = true
                        locationPermissionLauncher.launch(
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    }
                }

                AppNavGraph(
                    restaurantViewModel = restaurantViewModel,
                    locationViewModel = locationViewModel
                )
            }
        }
    }
}
