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
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bitewise.viewmodel.AuthVM
import com.example.bitewise.viewmodel.GenerateViewModel
import com.example.bitewise.view.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val authVM: AuthVM = viewModel()
    val vm: GenerateViewModel = viewModel()

    val isLoggedIn = authVM.loggedInEmail != null

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) "dashboard" else "login"
    ) {

        // Login
        composable("login") {
            LoginView(
                authVM = authVM,
                onLoginSuccess = { navController.navigate("dashboard") { popUpTo("login") { inclusive = true } } },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }

        // Register
        composable("register") {
            RegisterView(
                authVM = authVM,
                onRegisterSuccess = { navController.navigate("dashboard") { popUpTo("register") { inclusive = true } } },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        // Dashboard
        composable("dashboard") {
            DashboardScreen(
                vm = vm,
                authVM = authVM,
                onPlanClick = { navController.navigate("planDetail") },
                onGenerateClick = { navController.navigate("generate") },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                }
            )
        }

//        // Generate
//        composable("generate") {
//            GenerateScreen(
//                vm = vm,
//                onDetail = { navController.navigate("detail") },
//                onPlan = { navController.navigate("plan") },
//                onIngredientSelect = { navController.navigate("ingredientSelection") }
//            )
//        }
//
//        // Ingredient picker
//        composable("ingredientSelection") {
//            IngredientSelectionScreen(
//                onBack = { navController.popBackStack() },
//                onSearch = {
//                    vm.searchByIngredients(it)
//                    navController.popBackStack()
//                }
//            )
//        }
        // Generate Screen - Modify to add a new navigation callback
        composable("generate") {
            GenerateScreen(
                vm = vm,
                onDetail = { navController.navigate("detail") },
                onPlan = { navController.navigate("plan") },
                onIngredientSelect = { navController.navigate("ingredientSelection") },
                // New navigation callback for the auto-generate feature
                onAutoGenerateIngredientSelect = { navController.navigate("autoGenerateIngredientSelection") }
            )
        }

        // Ingredient picker for regular search
        composable("ingredientSelection") {
            IngredientSelectionScreen(
                onBack = { navController.popBackStack() },
                onSearch = { ingredients ->
                    vm.searchByIngredients(ingredients) // Existing search by ingredients
                    navController.popBackStack() // Go back to GenerateScreen to show results
                }
            )
        }

        // New: Ingredient picker for Auto-Generate Plan
        composable("autoGenerateIngredientSelection") {
            IngredientSelectionScreen(
                onBack = { navController.popBackStack() },
                onSearch = { ingredients ->
                    vm.autoGeneratePlanFromIngredients(ingredients)
                    // Navigate to the plan screen to see the auto-populated plan,
                    // or back to generate screen if you want to show the message there.
                    // Let's navigate to the plan screen.
                    navController.navigate("plan") {
                        popUpTo("autoGenerateIngredientSelection") { inclusive = true }
                        // Optionally popUpTo("generate") as well if coming from there
                    }
                }
            )
        }

        // Detail from Generate
        composable("detail") {
            MealDetailScreen(
                vm = vm,
                onBack = { navController.popBackStack() },
                fromSaved = false
            )
        }

        // Current in-progress plan
        composable("plan") {
            MealPlanScreen(
                vm = vm,
                onSave = {
                    vm.saveCurrentPlan()
                    navController.popBackStack("dashboard", false)
                },
                onBack = { navController.popBackStack() }
            )
        }

        // Saved-plan overview
        composable("planDetail") {
            PlanDetailScreen(
                vm = vm,
                onBack = { navController.popBackStack() },
                onMealClick = { navController.navigate("detailFromPlan") }
            )
        }

        // Detail from Saved-plan
        composable("detailFromPlan") {
            MealDetailScreen(
                vm = vm,
                onBack = { navController.popBackStack() },
                fromSaved = true
            )
        }
    }
}
