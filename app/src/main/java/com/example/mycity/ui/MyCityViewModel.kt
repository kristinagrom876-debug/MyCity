package com.example.mycity.ui

import androidx.lifecycle.ViewModel
import com.example.mycity.data.Category
import com.example.mycity.data.LocalDataSource
import com.example.mycity.data.Place
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MyCityViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MyCityUiState())
    val uiState: StateFlow<MyCityUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(categories = LocalDataSource.categories) }
    }

    fun selectCategory(category: Category?) {
        _uiState.update { it.copy(selectedCategory = category, selectedPlace = null) }
    }

    fun selectPlace(place: Place?) {
        _uiState.update { it.copy(selectedPlace = place) }
    }
}