package com.example.eazyfind.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.eazyfind.ui.themes.AppBackground

@Composable
fun LocationSearchBar(
    location: String,
    onLocationClick: () -> Unit,
    onFilterClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        // Location selector
        Surface(
            onClick = onLocationClick,
            shape = RoundedCornerShape(12.dp),
            color = Color.Transparent
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = "Location",
                    tint = AppBackground
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = location,
                    style = MaterialTheme.typography.titleMedium,
                    color = AppBackground
                )
            }
        }

        Spacer(modifier = Modifier.width(100.dp))

        // Filter icon
        IconButton(onClick = onFilterClick) {
            Icon(
                imageVector = Icons.Filled.FilterAlt,
                contentDescription = "Filter",
                tint = AppBackground
            )
        }
    }
}