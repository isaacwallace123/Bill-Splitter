package com.example.billsplitting.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onMapClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bill Splitter") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            StepCard(
                step = "Step 1",
                title = "Choose a Location",
                description = "Open the map to pick where the bill is being split.",
                actionLabel = "Open Map",
                onClick = onMapClick
            )

            StepCard(
                step = "Step 2",
                title = "Create a New Bill",
                description = "Once you're on the map, tap to start a new bill with selected users.",
                enabled = false // Already implied, handled from map screen
            )

            StepCard(
                step = "Step 3",
                title = "Enjoy Simpler Splitting",
                description = "Everyone can see what they owe, mark payments, and stay on track.",
                enabled = false
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onSettingsClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Settings")
            }
        }
    }
}

@Composable
fun StepCard(
    step: String,
    title: String,
    description: String,
    actionLabel: String? = null,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(step, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray)
            Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(description, fontSize = 14.sp)

            if (actionLabel != null && onClick != null && enabled) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(actionLabel)
                }
            }
        }
    }
}