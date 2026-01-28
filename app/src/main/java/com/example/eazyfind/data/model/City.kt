package com.example.eazyfind.data.model

import com.google.gson.annotations.SerializedName

data class City(
    val id: Int,

    @SerializedName("city_name") // 👈 backend field name
    val name: String?
)
