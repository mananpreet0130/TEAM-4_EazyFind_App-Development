package com.example.eazyfind.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eazyfind.data.model.Restaurant
import com.example.eazyfind.data.remote.RetrofitInstance
import com.example.eazyfind.ui.filters.RestaurantFilter
import kotlinx.coroutines.launch

class RestaurantViewModel : ViewModel() {

    // 🔹 API-loaded list
    private val allRestaurants = mutableListOf<Restaurant>()

    // 🔹 Exposed list
    val restaurants = mutableStateOf<List<Restaurant>>(emptyList())

    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)
    val loadedCount = mutableStateOf(0)

    private var currentPage = 1
    private var hasMore = true

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
    }

    fun fetchNextPage(
        city: String?,
        name: String?,
        filter: RestaurantFilter
    ) {
        if (isLoading.value || !hasMore) return

        viewModelScope.launch {
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

                if (newItems.isEmpty()) {
                    hasMore = false
                } else {
                    allRestaurants.addAll(newItems)
                    restaurants.value = allRestaurants
                    loadedCount.value = restaurants.value.size
                    currentPage++
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
}
