package com.example.bitewise.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bitewise.viewmodel.AuthVM
import com.example.bitewise.viewmodel.GenerateViewModel
// import com.example.bitewise.view.* // Already have specific imports

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

        // Generate Screen
        composable("generate") {
            GenerateScreen(
                vm = vm,
                onDetail = { navController.navigate("detail") },
                onPlan = { navController.navigate("plan") },
                onIngredientSelect = { navController.navigate("ingredientSelection") },
                onAutoGenerateIngredientSelect = { navController.navigate("autoGenerateIngredientSelection") },
                onBack = { navController.popBackStack() } // Added this line
            )
        }

        // Ingredient picker for regular search/filter
        composable("ingredientSelection") {
            IngredientSelectionScreen(
                onBack = { navController.popBackStack() },
                mode = IngredientSelectionMode.SEARCH_FILTER, // Pass mode
                onSearch = { ingredients ->
                    vm.searchByIngredients(ingredients)
                    navController.popBackStack()
                }
            )
        }

        // New: Ingredient picker for Auto-Generate Plan
        composable("autoGenerateIngredientSelection") {
            IngredientSelectionScreen(
                onBack = { navController.popBackStack() },
                mode = IngredientSelectionMode.AUTO_GENERATE_PLAN, // Pass mode
                onSearch = { ingredients ->
                    vm.autoGeneratePlanFromIngredients(ingredients)
                    // Navigate to the plan screen to see the auto-populated plan,
                    // or back to generate screen if you want to show the message there.
                    // Let's navigate to the plan screen.
                    navController.navigate("plan") {
                        // Pop this screen off the stack
                        popUpTo("autoGenerateIngredientSelection") { inclusive = true }
                        // Optionally popUpTo("generate") as well if coming from there,
                        // but the current autoGeneratePlanFromIngredients does not clear searchResults
                        // on GenerateScreen, so staying on plan seems cleaner for this flow.
                        // If GenerateScreen should be the intermediate, then:
                        // popUpTo("generate") { inclusive = false }
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
                    // After saving, go back to dashboard, clearing the plan creation flow
                    navController.popBackStack("dashboard", inclusive = false)
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