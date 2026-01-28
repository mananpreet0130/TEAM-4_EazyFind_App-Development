package com.example.eazyfind.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eazyfind.data.model.Cuisine
import com.example.eazyfind.data.remote.RetrofitInstance
import kotlinx.coroutines.launch

class CuisineViewModel : ViewModel() {

    val cuisines = mutableStateOf<List<Cuisine>>(emptyList())

    init {
        viewModelScope.launch {
            cuisines.value = RetrofitInstance.api.getCuisines()
        }
    }
}
