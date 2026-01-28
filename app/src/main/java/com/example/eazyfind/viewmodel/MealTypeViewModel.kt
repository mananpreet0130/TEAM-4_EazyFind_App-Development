package com.example.eazyfind.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eazyfind.data.model.MealType
import com.example.eazyfind.data.remote.RetrofitInstance
import kotlinx.coroutines.launch

class MealTypeViewModel : ViewModel() {

    val mealTypes = mutableStateOf<List<MealType>>(emptyList())

    init {
        viewModelScope.launch {
            mealTypes.value = RetrofitInstance.api.getMealTypes()
        }
    }
}
