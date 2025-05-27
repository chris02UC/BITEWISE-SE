package com.example.bitewise.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bitewise.model.Meal
import com.example.bitewise.viewmodel.GenerateViewModel

@Composable
fun AppNavHost() {
    val nav = rememberNavController()
    val vm: GenerateViewModel = viewModel()

    NavHost(navController = nav, startDestination = "dashboard") {
        composable("dashboard") {
            DashboardScreen(
                plans = vm.savedPlans,
                onGenerateClick = { nav.navigate("generate") }
            )
        }
        composable("generate") {
            GenerateScreen(
                vm = vm,
                onDetail    = { nav.navigate("detail") },
                onPlan      = { nav.navigate("plan") },
                onIngredientSelect = { nav.navigate("ingredientSelection") }
            )
        }
        composable("ingredientSelection") {
            IngredientSelectionScreen(
                onBack   = { nav.popBackStack() },
                       onSearch = { selected ->
                               vm.searchByIngredients(selected)
                               nav.popBackStack()
                }
            )
        }
        composable("detail") {
            MealDetailScreen(vm = vm, onBack = { nav.popBackStack() })
        }
        composable("plan") {
            MealPlanScreen(
                vm = vm,
                onSave = {
                    vm.saveCurrentPlan()
                    nav.popBackStack("dashboard", inclusive = false)
                },
                onBack = { nav.popBackStack() }
            )
        }
    }
}