package com.example.eazyfind.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eazyfind.data.model.Restaurant
import com.example.eazyfind.data.remote.RetrofitInstance
import com.example.eazyfind.ui.filters.RestaurantFilter
import kotlinx.coroutines.launch
import android.util.Log
import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RestaurantViewModel : ViewModel() {

    // API-loaded list
    private val allRestaurants = mutableListOf<Restaurant>()

    // Exposed list
    val restaurants = mutableStateOf<List<Restaurant>>(emptyList())

    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)
    val loadedCount = mutableStateOf(0)

    private var currentPage = 1
    private var hasMore = true

    val hasMoreData = mutableStateOf(true)

    val detectedCity = mutableStateOf<String?>(null)

    private val TAG = "RestaurantFetch"

    fun resetAndFetch(
        city: String?,
        name: String?,
        filter: RestaurantFilter
    ) {
        currentPage = 1
        hasMore = true
        allRestaurants.clear()
        restaurants.value = emptyList()
        loadedCount.value = 0
        fetchNextPage(city, name, filter)
        Log.d(
            TAG,
            "FETCH TRIGGER | city=$city | name=$name | " +
                    "cost=${filter.minCost}-${filter.maxCost} | " +
                    "rating=${filter.rating} | discount=${filter.discount} | " +
                    "mealIds=${filter.mealIds} | cuisineIds=${filter.cuisineIds}"
        )
    }

    fun fetchNextPage(
        city: String?,
        name: String?,
        filter: RestaurantFilter
    ) {
        if (isLoading.value || !hasMore) return

        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO){
            try {
                isLoading.value = true
                errorMessage.value = null

                val response = RetrofitInstance.api.getRestaurants(
                    city = city,
                    name = name,
                    minCost = filter.minCost,
                    maxCost = filter.maxCost,
                    rating = filter.rating.takeIf { it > 0f },
                    discount = filter.discount.takeIf { it > 0 },
                    mealIds =
                        filter.mealIds.takeIf { it.isNotEmpty() }?.joinToString(","),
                    cuisineIds =
                        filter.cuisineIds.takeIf { it.isNotEmpty() }?.joinToString(","),
                    page = currentPage
                )

                val newItems = response.restaurants

                Log.d(
                    TAG,
                    "API PAGE=$currentPage | fetched=${newItems.size} restaurants"
                )

                if (newItems.isEmpty()) {
                    hasMore = false
                    hasMoreData.value = false
                } else {
                    allRestaurants.addAll(newItems)
                    restaurants.value = allRestaurants.toList()
                    loadedCount.value = restaurants.value.size
                    currentPage++
                    Log.d(
                        TAG,
                        "PAGE = ${currentPage-1} | TOTAL LOADED=${allRestaurants.size} | hasMore=$hasMore"
                    )
                    hasMoreData.value = true
                }

            } catch (e: Exception) {

                when (e) {
                    is java.net.SocketTimeoutException -> {
                        errorMessage.value = "Server is waking up. Please try again."
                        hasMore = false
                    }

                    is retrofit2.HttpException -> {
                        errorMessage.value = "Server error (${e.code()})"
                        hasMore = false
                    }

                    else -> {
                        errorMessage.value = e.message ?: "Something went wrong"
                    }
                }
            } finally {
                isLoading.value = false
            }
        }
    }
    @SuppressLint("MissingPermission")
    fun detectCityFromLocation(context: Context) {

        val fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(context)

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->

            if (location == null) {
                Log.e("CityDetect", "Location is null")
                return@addOnSuccessListener
            }

            val lat = location.latitude
            val lon = location.longitude

            Log.d("CityDetect", "Lat=$lat , Lon=$lon")

            viewModelScope.launch {
                try {
                    val response = RetrofitInstance.api.getCityFromLatLon(
                        lat = lat,
                        lon = lon
                    )

                    val cityName = response.city   // 👈 depends on API response

                    detectedCity.value = cityName

                    Log.d("CityDetect", "Detected city = $cityName")

                } catch (e: Exception) {
                    Log.e("CityDetect", "City detection failed", e)
                }
            }
        }
    }

}