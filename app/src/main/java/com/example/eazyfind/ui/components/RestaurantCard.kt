package com.example.eazyfind.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PeopleOutline
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.eazyfind.data.model.Restaurant
import com.example.eazyfind.ui.themes.DarkText
import com.example.eazyfind.ui.themes.PrimaryColor
import com.example.eazyfind.ui.themes.SuccessGreen

private fun formatCityForUi(value: String): String {
    val cleaned = value
        .lowercase()
        .replace("-", " ")
        .trim()

    if (cleaned == "ncr") return "NCR"

    return cleaned
        .split(" ")
        .filter { it.isNotBlank() }
        .joinToString(" ") { word ->
            if (word == "ncr") "NCR"
            else word.replaceFirstChar { it.uppercase() }
        }
}


private fun getAreaAndCity(
    area: String?,
    city: String?
): Pair<String?, String?> {

    val rawCity = city?.takeIf { it.isNotBlank() }
    val formattedCity = rawCity?.let { formatCityForUi(it) }

    val cleanedArea = area?.takeIf { it.isNotBlank() }?.let { rawArea ->
        if (rawCity == null) {
            rawArea
        } else {
            val parts = rawArea.split(",")
            val lastPart = parts.lastOrNull()?.trim()

            if (lastPart != null && lastPart.equals(rawCity, ignoreCase = true)) {
                parts.dropLast(1).joinToString(",").trim()
            } else {
                rawArea
            }
        }
    }

    return cleanedArea to formattedCity
}

@Composable
fun RestaurantCard(
    restaurant: Restaurant,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .height(270.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF2EED3)
        ),
        shape = RoundedCornerShape(5.dp)
    ) {

        Column (
            modifier = Modifier.fillMaxHeight()
        ) {

            // IMAGE FROM API
            AsyncImage(
                model = restaurant.image_url,
                contentDescription = restaurant.restaurant_name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(8.dp)) {

                // NAME
                Text(
                    text = restaurant.restaurant_name,
                    style = MaterialTheme.typography.titleLarge,
                    color = DarkText,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                val (areaText, cityText) = getAreaAndCity(
                    area = restaurant.area,
                    city = restaurant.city
                )

                // LINE 1: AREA + PRICE
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // AREA
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    ) {
                        areaText?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium,
                                color = DarkText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // PRICE
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PeopleOutline,
                                contentDescription = "Cost for two",
                                modifier = Modifier.size(13.dp),
                                tint = DarkText
                            )

                            Spacer(modifier = Modifier.width(1.dp))

                            Text(
                                text = "₹${restaurant.cost_for_two}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = DarkText
                            )
                        }
                    }

                }

                Spacer(modifier = Modifier.height(4.dp))

                // LINE 2: CITY + RATING
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    cityText?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.StarBorder,
                                contentDescription = "Rating",
                                modifier = Modifier.size(11.dp),
                                tint = PrimaryColor
                            )

                            Text(
                                text = "${restaurant.rating}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = PrimaryColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // 🟢 OFFER
                restaurant.offer?.let { offer ->
                    if (offer.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, SuccessGreen),
                            color = Color.Transparent
                        ) {
                            Text(
                                text = offer,
                                style = MaterialTheme.typography.bodyLarge,
                                color = SuccessGreen,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
