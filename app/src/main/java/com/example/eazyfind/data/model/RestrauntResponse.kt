package com.example.eazyfind.data.model

import com.google.gson.annotations.SerializedName

data class RestaurantResponse(

    @SerializedName("restaurants")
    val restaurants: List<Restaurant>
)
