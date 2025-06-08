package com.example.bitewise.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bitewise.viewmodel.AuthVM
import com.example.bitewise.viewmodel.GenerateVM

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val authVM: AuthVM = viewModel()
    val vm: GenerateVM = viewModel()

    val currentUserId = authVM.currentUserId
    val initialDestination = if (currentUserId != null) "dashboard" else "login"

    LaunchedEffect(authVM.currentUserId) {
        val userId = authVM.currentUserId
        if (userId != null) {
            vm.reinitializeForUser(userId)
        }
    }

    NavHost(
        navController = navController,
        startDestination = initialDestination
    ) {

        composable("login") {
            LoginView(
                authVM = authVM,
                onLoginSuccess = {
                    authVM.currentUserId?.let { userId ->
                        vm.reinitializeForUser(userId)
                    }
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }

        composable("register") {
            RegisterView(
                authVM = authVM,
                onRegisterSuccess = {
                    authVM.currentUserId?.let { userId ->
                        vm.reinitializeForUser(userId)
                    }
                    navController.navigate("dashboard") {
                        popUpTo("register") { inclusive = true }
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable("dashboard") {
            DashboardView(
                vm = vm,
                authVM = authVM,
                onPlanClick = { navController.navigate("planDetail") },
                onGenerateClick = { navController.navigate("generate") },
                onLogout = {
                    authVM.logout {
                        vm.clearUserSpecificData()
                        navController.navigate("login") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }

        composable("generate") {
            GenerateView(
                vm = vm,
                onDetail = { navController.navigate("detail") },
                onPlan = { navController.navigate("plan") },
                onIngredientSelect = { navController.navigate("ingredientSelection") },
                onAutoGenerateIngredientSelect = { navController.navigate("autoGenerateIngredientSelection") },
                onBack = { navController.popBackStack() }
            )
        }

        composable("ingredientSelection") {
            IngredientSelectionView(
                onBack = { navController.popBackStack() },
                mode = IngredientSelectionMode.SEARCH_FILTER,
                onSearch = { ingredients ->
                    vm.searchByIngredients(ingredients)
                    navController.popBackStack()
                }
            )
        }

        composable("autoGenerateIngredientSelection") {
            IngredientSelectionView(
                onBack = { navController.popBackStack() },
                mode = IngredientSelectionMode.AUTO_GENERATE_PLAN,
                onSearch = { ingredients ->
                    vm.autoGeneratePlanFromIngredients(ingredients)
                    navController.navigate("plan") {
                        popUpTo("autoGenerateIngredientSelection") { inclusive = true }
                    }
                }
            )
        }

        composable("detail") {
            MealDetailView(
                vm = vm,
                onBack = { navController.popBackStack() },
                fromSaved = false
            )
        }

        composable("plan") { // This is the MealPlanView route
            MealPlanView(
                vm = vm,
                onSave = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vm.saveCurrentPlan() // This attempts to save the plan
                    }
                    // Restored navigation back to dashboard after attempting to save
                    navController.popBackStack("dashboard", inclusive = false)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("planDetail") {
            PlanDetailView(
                vm = vm,
                onBack = { navController.popBackStack() },
                onMealClick = { navController.navigate("detailFromPlan") }
            )
        }

        composable("detailFromPlan") {
            MealDetailView(
                vm = vm,
                onBack = { navController.popBackStack() },
                fromSaved = true
            )
        }
    }
}