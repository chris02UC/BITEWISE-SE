package com.example.bitewise.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bitewise.model.Meal
import com.example.bitewise.model.MealPlan
import com.example.bitewise.viewmodel.GenerateViewModel
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardScreen(
    vm: GenerateViewModel,
    authVM: com.example.bitewise.viewmodel.AuthVM,
    onPlanClick: (MealPlan) -> Unit,
    onGenerateClick: () -> Unit,
    onLogout: () -> Unit
) {
    val plans = vm.savedPlans

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // 🔓 Logout 버튼
        Button(
            onClick = {
                authVM.logout()
                onLogout()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout")
        }


        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onGenerateClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Generate Meal Plan")
        }

        Spacer(Modifier.height(24.dp))

        Text("Saved Meal Plans", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        if (plans.isEmpty()) {
            Text("No saved plans yet.", style = MaterialTheme.typography.bodyMedium)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(plans) { plan ->
                    Card(modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            vm.selectPlan(plan)
                            onPlanClick(plan)
                        }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Plan #${plan.id}", style = MaterialTheme.typography.titleSmall)
                            Text(
                                plan.dateCreated.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(Modifier.height(8.dp))
                            plan.meals.forEach { meal ->
                                Text("• ${meal.name}", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}
