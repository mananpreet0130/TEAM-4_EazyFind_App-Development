package com.example.eazyfind.data.model

import com.google.gson.annotations.SerializedName

data class MealType(
    val id: String,

    @SerializedName("meal_type")
    val name: String
)
