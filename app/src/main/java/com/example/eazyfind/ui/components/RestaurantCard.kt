package com.example.eazyfind.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.eazyfind.data.model.Restaurant
import com.example.eazyfind.ui.themes.DarkText
import com.example.eazyfind.ui.themes.SuccessGreen
import com.example.eazyfind.ui.utils.normalizeCity

private fun formatCityForUi(value: String): String {
    return normalizeCity(value)
        .split(" ")
        .joinToString(" ") { word ->
            when (word) {
                "ncr" -> "NCR"   // 🔒 force uppercase
                else -> word.replaceFirstChar { it.uppercase() }
            }
        }
}

private fun getAreaAndCity(
    area: String?,
    city: String?
): Pair<String?, String?> {

    val formattedCity = city
        ?.takeIf { it.isNotBlank() }
        ?.let { formatCityForUi(it) }

    val formattedArea = area
        ?.takeIf { it.isNotBlank() }
        ?.let { rawArea ->

            if (formattedCity == null) {
                formatCityForUi(rawArea)
            } else {
                val parts = rawArea.split(",").map { it.trim() }

                val cleanedParts =
                    if (parts.lastOrNull()?.equals(formattedCity, ignoreCase = true) == true) {
                        parts.dropLast(1)
                    } else {
                        parts
                    }

                formatCityForUi(cleanedParts.joinToString(", "))
            }
        }

    return formattedArea to formattedCity
}

@Composable
fun RestaurantCard(
    restaurant: Restaurant,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF2EED3)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {

        Column {

            // 🌐 IMAGE FROM API
            AsyncImage(
                model = restaurant.image_url,
                contentDescription = restaurant.restaurant_name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(8.dp)) {

                // 🍽️ NAME
                Text(
                    text = restaurant.restaurant_name,
                    style = MaterialTheme.typography.titleLarge,
                    color = DarkText
                )

                Spacer(modifier = Modifier.height(4.dp))

                // AREA + COST
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // 📍 AREA + CITY
                    val (areaText, cityText) = getAreaAndCity(
                        area = restaurant.area,
                        city = restaurant.city
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        ) {

                            // 📍 AREA (Line 1)
                            areaText?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = DarkText
                                )
                            }

                            // 🌆 CITY (Line 2)
                            cityText?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = DarkText
                                )
                            }
                        }

                        // 👥 COST FOR TWO (always stable)
                        Text(
                            text = "👥 ₹${restaurant.cost_for_two}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // OFFER + RATING
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // 🟢 OFFER
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, SuccessGreen),
                        color = Color.Transparent
                    ) {
                        restaurant.offer?.let { offer ->
                            if (offer.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = offer,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = SuccessGreen,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    // ⭐ RATING
                    Text(
                        text = "⭐ ${restaurant.rating}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkText
                    )
                }
            }
        }
    }
}
