package com.example.bitewise.view

//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.mutableStateListOf
//import androidx.compose.runtime.remember
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import com.example.bitewise.model.Meal
//import com.example.bitewise.viewmodel.GenerateViewModel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bitewise.viewmodel.GenerateViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost() {
    val nav = rememberNavController()
    val vm: GenerateViewModel = viewModel()

    NavHost(navController = nav, startDestination = "dashboard") {
        // Dashboard
        composable("dashboard") {
            DashboardScreen(
                vm = vm,
                onPlanClick     = { nav.navigate("planDetail") },
                onGenerateClick = { nav.navigate("generate") }
            )
        }
        // Generate flow
        composable("generate") {
            GenerateScreen(
                vm                  = vm,
                onDetail            = { nav.navigate("detail") },
                onPlan              = { nav.navigate("plan") },
                onIngredientSelect  = { nav.navigate("ingredientSelection") }
            )
        }
        // Ingredient picker
        composable("ingredientSelection") {
            IngredientSelectionScreen(
                onBack   = { nav.popBackStack() },
                onSearch = {
                    vm.searchByIngredients(it)
                    nav.popBackStack()
                }
            )
        }
        // Detail from Generate
        composable("detail") {
            MealDetailScreen(
                vm        = vm,
                onBack    = { nav.popBackStack() },
                fromSaved = false
            )
        }
        // Current in-progress plan
        composable("plan") {
            MealPlanScreen(
                vm     = vm,
                onSave = {
                    vm.saveCurrentPlan()
                    nav.popBackStack("dashboard", false)
                },
                onBack = { nav.popBackStack() }
            )
        }
        // Saved-plan overview
        composable("planDetail") {
            PlanDetailScreen(
                vm          = vm,
                onBack      = { nav.popBackStack() },
                onMealClick = { nav.navigate("detailFromPlan") }
            )
        }
        // Detail from Saved-plan
        composable("detailFromPlan") {
            MealDetailScreen(
                vm        = vm,
                onBack    = { nav.popBackStack() },
                fromSaved = true
            )
        }
    }
}