package com.example.eazyfind.ui.utils

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices

@SuppressLint("MissingPermission")
fun getCurrentLocation(
    context: Context,
    onLocationReceived: (Double, Double) -> Unit
) {
    val fusedClient =
        LocationServices.getFusedLocationProviderClient(context)

    fusedClient.lastLocation
        .addOnSuccessListener { location ->
            if (location != null) {
                onLocationReceived(
                    location.latitude,
                    location.longitude
                )
            }
        }
}
