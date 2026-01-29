package com.example.eazyfind.ui.screens

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import android.net.Uri
import android.content.Context
import android.content.pm.PackageManager

fun openInChrome(context: Context, url: String) {
    val uri = Uri.parse(url)

    val chromePackage = "com.android.chrome"

    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
        addCategory(Intent.CATEGORY_BROWSABLE)
        setPackage(chromePackage)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }

    try {
        // Try opening in Chrome
        context.startActivity(intent)
    } catch (e: Exception) {
        // 🔁 Chrome not installed → fallback to system browser
        val fallbackIntent = Intent(Intent.ACTION_VIEW, uri).apply {
            addCategory(Intent.CATEGORY_BROWSABLE)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(fallbackIntent)
    }
}

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

    var paginationError by remember { mutableStateOf<String?>(null) }

    var isRequestingNextPage by remember { mutableStateOf(false) }

    val context = LocalContext.current

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

    /* ---------------- PAGINATION RESET GUARD ---------------- */

    LaunchedEffect(isLoading) {
        if (!isLoading) {
            isRequestingNextPage = false
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

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
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
                                city =
                                    if (selectedCity == "Select Location") null
                                    else mapCityForApi(selectedCity),
                                name = searchQuery.ifBlank { null },
                                filter = apiFilter
                            )
                        }

                        RestaurantCard(
                            restaurant = restaurant,
                            onClick = {
                                val url = restaurant.url ?: return@RestaurantCard

                                val finalUrl =
                                    if (url.startsWith("http://") || url.startsWith("https://"))
                                        url
                                    else
                                        "https://$url"

                                openInChrome(context, finalUrl)
                            }
                        )

                    }

                    // 🔄 LOADING MORE
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

                    // ❌ PAGINATION ERROR + RETRY
                    if (
                        !isLoading &&
                        error == null &&
                        restaurants.isNotEmpty() &&
                        !restaurantViewModel.hasMoreData.value
                    ) {
                        item(span = { GridItemSpan(2) }) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {                            }
                        }
                    }

                    // ✅ NO MORE RESTAURANTS
                    if (!isLoading && error == null && restaurants.isNotEmpty()) {
                        item(span = { GridItemSpan(2) }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "You’ve reached the end 🍽️",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = DarkText.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            }
        }
        /* ---------------- LOCATION SHEET ---------------- */

        if (showLocationSheet) {
            ModalBottomSheet(onDismissRequest = { showLocationSheet = false }) {
                val cities = locationViewModel.cities.value

                if (cities.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    cities
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
                                    .clickable(
                                        indication = LocalIndication.current,
                                        interactionSource = remember { MutableInteractionSource() }
                                    ) {
                                        locationViewModel.selectCity(city.name ?: "")
                                        showLocationSheet = false
                                    }
                            )
                        }
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
