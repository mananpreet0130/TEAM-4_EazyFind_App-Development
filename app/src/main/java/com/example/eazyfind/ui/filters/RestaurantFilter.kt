package com.example.eazyfind.ui.filters

data class RestaurantFilter(
    val minCost: Int = 0,
    val maxCost: Int = 10000,
    val rating: Float = 0f,
    val discount: Int = 0,
    val mealIds: Set<Int> = emptySet(),
    val cuisineIds: Set<Int> = emptySet()
)