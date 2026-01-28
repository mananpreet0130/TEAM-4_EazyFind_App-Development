package com.example.eazyfind.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FilterChipItem(
    text: String,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .background(
                color = Color(0xFFF2EED3),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(Modifier.width(8.dp))

        Text(
            text = "✕",
            modifier = Modifier.clickable { onRemove() },
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}
