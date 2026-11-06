package com.example.mycity.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCityApp(
    windowSize: WindowWidthSizeClass,
    viewModel: MyCityViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val navController = rememberNavController()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val title = when {
        windowSize == WindowWidthSizeClass.Expanded && uiState.selectedPlace != null -> uiState.selectedPlace!!.name
        windowSize == WindowWidthSizeClass.Expanded && uiState.selectedCategory != null -> uiState.selectedCategory!!.name
        currentRoute == "detail" -> uiState.selectedPlace?.name ?: "Детали"
        currentRoute == "places" -> uiState.selectedCategory?.name ?: "Места"
        else -> "My City"
    }

    val canNavigateBack = if (windowSize == WindowWidthSizeClass.Expanded) {
        uiState.selectedPlace != null || uiState.selectedCategory != null
    } else {
        navController.previousBackStackEntry != null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = title) },
                navigationIcon = {
                    if (canNavigateBack) {
                        IconButton(
                            onClick = {
                                if (windowSize == WindowWidthSizeClass.Expanded) {
                                    if (uiState.selectedPlace != null) {
                                        viewModel.selectPlace(null)
                                    } else {
                                        viewModel.selectCategory(null)
                                    }
                                } else {
                                    navController.navigateUp()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Назад"
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (windowSize == WindowWidthSizeClass.Expanded) {
            Row(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                CategoryListScreen(
                    categories = uiState.categories,
                    onCategoryClick = { viewModel.selectCategory(it) },
                    modifier = Modifier.weight(1f)
                )

                if (uiState.selectedCategory != null) {
                    if (uiState.selectedPlace == null) {
                        PlaceListScreen(
                            category = uiState.selectedCategory!!,
                            onPlaceClick = { viewModel.selectPlace(it) },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        PlaceDetailScreen(
                            place = uiState.selectedPlace!!,
                            modifier = Modifier.weight(1f)
                        )
                    }
                } else {
                    Text(
                        text = "Выберите категорию",
                        modifier = Modifier.weight(1f).padding(16.dp)
                    )
                }
            }
        } else {
            NavHost(
                navController = navController,
                startDestination = "categories",
                modifier = Modifier.padding(paddingValues)
            ) {
                composable("categories") {
                    CategoryListScreen(
                        categories = uiState.categories,
                        onCategoryClick = { category ->
                            viewModel.selectCategory(category)
                            navController.navigate("places")
                        }
                    )
                }
                composable("places") {
                    val selectedCategory = uiState.selectedCategory
                    if (selectedCategory != null) {
                        PlaceListScreen(
                            category = selectedCategory,
                            onPlaceClick = { place ->
                                viewModel.selectPlace(place)
                                navController.navigate("detail")
                            }
                        )
                    }
                }
                composable("detail") {
                    val place = uiState.selectedPlace
                    if (place != null) {
                        PlaceDetailScreen(place = place)
                    }
                }
            }
        }
    }
}