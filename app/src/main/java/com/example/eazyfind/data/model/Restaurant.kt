package com.example.eazyfind.data.model

import com.google.gson.annotations.SerializedName

data class Restaurant(

    val id: String,

    @SerializedName("restaurant_name")
    val restaurant_name: String,

    val city: String? = null,

    val area: String? = null,

    val cost_for_two: Int = 0,

    val rating: String? = null,

    val offer: String? = null,

    val effective_discount: Float = 0f,

    val latitude: Double = 0.0,

    val longitude: Double = 0.0,

    val image_url: String? = null,

    // 🔒 NEVER NULL — FIXES ALL CRASHES
    val cuisines: List<Cuisine> = emptyList(),

    // 🔒 NEVER NULL — FIXES ALL CRASHES
    val meal_types: List<MealType> = emptyList()
)
