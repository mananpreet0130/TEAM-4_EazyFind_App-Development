package com.example.eazyfind.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eazyfind.ui.components.*
import com.example.eazyfind.ui.filters.RestaurantFilter
import com.example.eazyfind.ui.filters.RestaurantFilterSheet
import com.example.eazyfind.ui.themes.AppBackground
import com.example.eazyfind.ui.themes.DarkText
import com.example.eazyfind.ui.utils.mapCityForApi
import com.example.eazyfind.viewmodel.*

private fun formatCityName(raw: String): String {
    return raw
        .lowercase()
        .split("-", " ")
        .filter { it.isNotBlank() }
        .joinToString(" ") { word ->
            when (word) {
                "ncr" -> "NCR"
                else -> word.replaceFirstChar { it.uppercase() }
            }
        }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    restaurantViewModel: RestaurantViewModel = viewModel(),
    locationViewModel: LocationViewModel = viewModel(),
    cuisineViewModel: CuisineViewModel = viewModel(),
    mealTypeViewModel: MealTypeViewModel = viewModel(),
    onRestaurantClick: (String) -> Unit = {}
) {

    val restaurants = restaurantViewModel.restaurants.value
    val isLoading = restaurantViewModel.isLoading.value
    val error = restaurantViewModel.errorMessage.value
    val loadedCount = restaurantViewModel.loadedCount.value

    val selectedCity = locationViewModel.selectedCity.value

    var searchQuery by remember { mutableStateOf("") }
    var showLocationSheet by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }

    var apiFilter by remember { mutableStateOf(RestaurantFilter()) }

    /* ---------------- SEARCH ---------------- */

    LaunchedEffect(searchQuery) {
        kotlinx.coroutines.delay(500)
        restaurantViewModel.resetAndFetch(
            city =
                if (selectedCity == "Select Location") null
                else mapCityForApi(selectedCity),
            name = searchQuery.ifBlank { null },
            filter = apiFilter
        )
    }

    /* ---------------- CITY + FILTER TRIGGER ---------------- */

    LaunchedEffect(selectedCity, apiFilter) {
        restaurantViewModel.resetAndFetch(
            city =
                if (selectedCity == "Select Location") null
                else mapCityForApi(selectedCity),
            name = searchQuery.ifBlank { null },
            filter = apiFilter
        )
    }

    /* ---------------- ENSURE FILTER DATA IS READY ---------------- */

    LaunchedEffect(Unit) {
        cuisineViewModel.cuisines.value
        mealTypeViewModel.mealTypes.value
    }

    /* ---------------- UI ---------------- */

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {

        Column {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF7F6759))
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF7F6759)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(bottom = 6.dp)
                            .padding(top=24.dp)
                            .padding(horizontal = 8.dp)
                    ) {

                        LocationSearchBar(
                            location =
                                if (selectedCity == "Select Location")
                                    selectedCity
                                else
                                    formatCityName(selectedCity),
                            onLocationClick = { showLocationSheet = true },
                            onFilterClick = { showFilterSheet = true }
                        )

                        SearchBar(
                            query = searchQuery,
                            onQueryChange = { searchQuery = it }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp)
            ) {

                if (isLoading && loadedCount > 0) {
                    Text(
                        text = "Loaded $loadedCount restaurants",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkText.copy(alpha = 0.7f)
                    )
                }

                if (error != null) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                if (!isLoading && restaurants.isEmpty() && error == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No restaurants found with the selected filters",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkText.copy(alpha = 0.7f)
                        )
                    }
                }


                LazyColumn {

                    itemsIndexed(restaurants) { index, restaurant ->

                        if (index == restaurants.lastIndex && !isLoading) {
                            restaurantViewModel.fetchNextPage(
                                city =
                                    if (selectedCity == "Select Location") null
                                    else mapCityForApi(selectedCity),
                                name = searchQuery.ifBlank { null },
                                filter = apiFilter
                            )
                        }

                        RestaurantCard(
                            restaurant = restaurant,
                            onClick = { onRestaurantClick(restaurant.id) }
                        )
                    }

                    if (isLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
        /* ---------------- LOCATION SHEET ---------------- */

        if (showLocationSheet) {
            ModalBottomSheet(onDismissRequest = { showLocationSheet = false }) {
                locationViewModel.cities.value
                    .sortedBy { formatCityName(it.name ?: "") }
                    .forEach { city ->
                    ListItem(
                        headlineContent = {
                            Text(
                                text = formatCityName(city.name ?: ""),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.titleMedium
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                locationViewModel.selectCity(city.name ?: "")
                                showLocationSheet = false
                            }
                    )
                }
                Spacer(Modifier.height(30.dp))
            }
        }

        /* ---------------- FILTER SHEET ---------------- */

        if (showFilterSheet) {
            ModalBottomSheet(onDismissRequest = { showFilterSheet = false }) {
                RestaurantFilterSheet(
                    filter = apiFilter,
                    cuisines = cuisineViewModel.cuisines.value,
                    mealTypes = mealTypeViewModel.mealTypes.value,
                    restaurants = restaurants,
                    onFilterChange = { apiFilter = it },
                    onClose = { showFilterSheet = false }
                )
            }
        }
    }
}
