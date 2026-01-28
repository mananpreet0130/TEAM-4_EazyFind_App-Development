package com.example.eazyfind.ui.filters

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.eazyfind.data.model.Cuisine
import com.example.eazyfind.data.model.MealType
import com.example.eazyfind.ui.themes.PrimaryFilterChipColors
import kotlinx.coroutines.delay

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun RestaurantFilterSheet(
    filter: RestaurantFilter,
    cuisines: List<Cuisine>,
    mealTypes: List<MealType>,
    restaurants: List<Any>, // kept for compatibility
    onFilterChange: (RestaurantFilter) -> Unit,
    onClose: () -> Unit
) {

    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(visible) {
        if (!visible) {
            delay(200) // matches fadeOut default duration
            onClose()
        }
    }

    AnimatedVisibility(
        visible = visible,
        exit = fadeOut()
    ) {

        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {

            /* ---------------- STICKY HEADER ---------------- */

            stickyHeader {
                Surface {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = "Filters",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f)
                        )

                        TextButton(
                            onClick = { onFilterChange(RestaurantFilter()) }
                        ) {
                            Text("Clear All")
                        }

                        IconButton(
                            onClick = {
                                visible = false
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close filters"
                            )
                        }
                    }
                }
            }

            /* ---------------- COST FOR TWO ---------------- */

            item {
                Spacer(Modifier.height(16.dp))
                Text("Cost for Two", style = MaterialTheme.typography.titleMedium)

                RangeSlider(
                    value = filter.minCost.toFloat()..filter.maxCost.toFloat(),
                    onValueChange = {
                        onFilterChange(
                            filter.copy(
                                minCost = it.start.toInt(),
                                maxCost = it.endInclusive.toInt()
                            )
                        )
                    },
                    valueRange = 0f..10000f
                )

                Text("₹${filter.minCost} - ₹${filter.maxCost}")
            }

            /* ---------------- RATING ---------------- */

            item {
                Spacer(Modifier.height(16.dp))
                Text("Minimum Rating", style = MaterialTheme.typography.titleMedium)

                Slider(
                    value = filter.rating,
                    onValueChange = {
                        onFilterChange(filter.copy(rating = it))
                    },
                    valueRange = 0f..5f,
                    steps = 4
                )

                Text(
                    if (filter.rating.toInt() < 5)
                        "${filter.rating.toInt()}★ & above"
                    else
                        "${filter.rating.toInt()}★"
                )
            }

            /* ---------------- DISCOUNT ---------------- */

            item {
                Spacer(Modifier.height(16.dp))
                Text("Minimum Discount", style = MaterialTheme.typography.titleMedium)

                Slider(
                    value = filter.discount.toFloat(),
                    onValueChange = {
                        onFilterChange(filter.copy(discount = it.toInt()))
                    },
                    valueRange = 0f..100f
                )

                Text(
                    text =
                        if (filter.discount == 0)
                            "All discounts"
                        else if(filter.discount==100)
                            "${filter.discount}%"
                        else
                            "${filter.discount}% & above",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            /* ---------------- MEAL TYPES ---------------- */

            item {
                Spacer(Modifier.height(16.dp))
                Text("Meal Types", style = MaterialTheme.typography.titleMedium)

                FlowRow {
                    mealTypes.forEach { meal ->
                        FilterChip(
                            selected = filter.mealIds.contains(meal.id.toInt()),
                            onClick = {
                                val updated =
                                    if (filter.mealIds.contains(meal.id.toInt()))
                                        filter.mealIds - meal.id.toInt()
                                    else
                                        filter.mealIds + meal.id.toInt()

                                onFilterChange(filter.copy(mealIds = updated))
                            },
                            label = { Text(meal.name ?: "") },
                            colors = PrimaryFilterChipColors(),
                            modifier = Modifier.padding(end = 8.dp, bottom = 8.dp)
                        )
                    }
                }
            }

            /* ---------------- CUISINES ---------------- */

            item {
                Spacer(Modifier.height(16.dp))
                Text("Cuisines", style = MaterialTheme.typography.titleMedium)

                FlowRow {
                    cuisines.forEach { cuisine ->
                        FilterChip(
                            selected = filter.cuisineIds.contains(cuisine.id.toInt()),
                            onClick = {
                                val updated =
                                    if (filter.cuisineIds.contains(cuisine.id.toInt()))
                                        filter.cuisineIds - cuisine.id.toInt()
                                    else
                                        filter.cuisineIds + cuisine.id.toInt()

                                onFilterChange(filter.copy(cuisineIds = updated))
                            },
                            label = { Text(cuisine.name ?: "") },
                            colors = PrimaryFilterChipColors(),
                            modifier = Modifier.padding(end = 8.dp, bottom = 8.dp)
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}