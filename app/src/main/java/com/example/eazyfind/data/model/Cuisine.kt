package com.example.eazyfind.data.model

import com.google.gson.annotations.SerializedName

data class Cuisine(
    val id: String,

    @SerializedName("cuisine_name")
    val name: String
)
