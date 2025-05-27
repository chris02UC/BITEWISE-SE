package com.example.bitewise.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bitewise.model.Meal

@Composable
fun DashboardScreen(
    plans: List<List<Meal>>,
    onGenerateClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = onGenerateClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Generate Meal Plan")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Saved Meal Plans", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(8.dp))

        if (plans.isEmpty()) {
            Text("No saved plans yet.", style = MaterialTheme.typography.bodyMedium)
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(plans) { index, plan ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { /* detail screen if desired */ }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Plan ${index + 1}", style = MaterialTheme.typography.titleSmall)
                            Spacer(Modifier.height(4.dp))
                            plan.forEach { meal ->
                                Text("• ${meal.name}", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}