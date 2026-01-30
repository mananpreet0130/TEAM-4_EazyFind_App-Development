package com.example.eazyfind.ui.screens

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.ui.platform.LocalContext
import com.example.eazyfind.ui.components.*
import com.example.eazyfind.ui.filters.RestaurantFilter
import com.example.eazyfind.ui.filters.RestaurantFilterSheet
import com.example.eazyfind.ui.themes.AppBackground
import com.example.eazyfind.ui.themes.DarkText
import com.example.eazyfind.ui.themes.PrimaryColor
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
    restaurantViewModel: RestaurantViewModel,
    locationViewModel: LocationViewModel,
    cuisineViewModel: CuisineViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    mealTypeViewModel: MealTypeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onRestaurantClick: (String) -> Unit = {}
) {

    val restaurants = restaurantViewModel.restaurants.value
    val isLoading = restaurantViewModel.isLoading.value
    val error = restaurantViewModel.errorMessage.value
    val selectedCity = locationViewModel.selectedCity.value

    var searchQuery by remember { mutableStateOf("") }
    var showLocationSheet by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }
    var apiFilter by remember { mutableStateOf(RestaurantFilter()) }
    var isRequestingNextPage by remember { mutableStateOf(false) }

    /* ---------------- SEARCH ---------------- */

    LaunchedEffect(searchQuery) {
        kotlinx.coroutines.delay(500)
        restaurantViewModel.resetAndFetch(
            city = if (selectedCity == "Select Location") null else mapCityForApi(selectedCity),
            name = searchQuery.ifBlank { null },
            filter = apiFilter
        )
    }

    /* ---------------- CITY + FILTER TRIGGER ---------------- */

    LaunchedEffect(selectedCity, apiFilter) {
        restaurantViewModel.resetAndFetch(
            city = if (selectedCity == "Select Location") null else mapCityForApi(selectedCity),
            name = searchQuery.ifBlank { null },
            filter = apiFilter
        )
    }

    /* ---------------- AUTO CITY SELECT ---------------- */

    val autoDetectedCity = restaurantViewModel.detectedCity.value

    LaunchedEffect(autoDetectedCity) {
        if (autoDetectedCity != null && selectedCity == "Select Location") {
            locationViewModel.selectCity(formatCityName(autoDetectedCity))
        }
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
                            .padding(top = 24.dp, bottom = 6.dp, start = 8.dp, end = 8.dp)
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

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp)
            ) {

                itemsIndexed(restaurants) { index, restaurant ->

                    if (
                        index >= restaurants.lastIndex - 2 &&
                        !isLoading &&
                        !isRequestingNextPage &&
                        restaurantViewModel.hasMoreData.value
                    ) {
                        isRequestingNextPage = true
                        restaurantViewModel.fetchNextPage(
                            city = if (selectedCity == "Select Location") null else mapCityForApi(selectedCity),
                            name = searchQuery.ifBlank { null },
                            filter = apiFilter
                        )
                    }

                    RestaurantCard(
                        restaurant = restaurant,
                        onClick = {
                            restaurant.url?.let { onRestaurantClick(it) }
                        }
                    )
                }

                if (isLoading && restaurants.isNotEmpty()) {
                    item(span = { GridItemSpan(2) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
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
                                    color = PrimaryColor,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    indication = LocalIndication.current,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) {
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
                    cuisines = cuisineViewModel.cuisines.value.sortedBy { it.name?.lowercase() },
                    mealTypes = mealTypeViewModel.mealTypes.value,
                    restaurants = restaurants,
                    onFilterChange = { apiFilter = it },
                    onClose = { showFilterSheet = false }
                )
            }
        }
    }
}
