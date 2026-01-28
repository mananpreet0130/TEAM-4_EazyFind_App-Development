package com.example.eazyfind.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eazyfind.data.model.City
import com.example.eazyfind.data.remote.RetrofitInstance
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import android.util.Log // Import Log

class LocationViewModel : ViewModel() {

    val cities = mutableStateOf<List<City>>(emptyList())
    val selectedCity = mutableStateOf("Select Location")

    init {
        viewModelScope.launch {
            cities.value = RetrofitInstance.api.getCities()
        }
    }

    fun selectCity(city: String) {
        selectedCity.value = city
    }
}