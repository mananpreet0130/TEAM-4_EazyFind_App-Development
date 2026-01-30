package com.example.eazyfind.data.remote

import com.example.eazyfind.data.model.*
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    /* -------- RESTAURANTS -------- */

    @GET("restaurants")
    suspend fun getRestaurants(
        @Query("city") city: String?,
        @Query("name") name: String?,
        @Query("minCost") minCost: Int?,
        @Query("maxCost") maxCost: Int?,
        @Query("rating") rating: Float?,
        @Query("discount") discount: Int?,
        @Query("mealIds") mealIds: String?,
        @Query("cuisineIds") cuisineIds: String?,
        @Query("page") page: Int
    ): RestaurantResponse

    /* -------- CITIES -------- */

    @GET("cities")
    suspend fun getCities(): List<City>


    /* -------- CUISINES -------- */

    @GET("cuisines")
    suspend fun getCuisines(): List<Cuisine>


    /* -------- MEAL TYPES -------- */

    @GET("meal-types")
    suspend fun getMealTypes(): List<MealType>

    data class CityResponse(
        val city: String
    )

    @GET("/cities/getCity")
    suspend fun getCityFromLatLon(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): CityResponse

}
