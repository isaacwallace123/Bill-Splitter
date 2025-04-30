package com.example.billsplitting.ui.screen

import Bill
import UserPayment
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.billsplitting.ui.viewmodel.BillViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import java.util.Date

@Composable
fun BillScreen(navController: NavController, viewModel: BillViewModel) {
    val bills = viewModel.bills

    LaunchedEffect(Unit) {
        viewModel.loadBills()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Your Bills", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        if (bills.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("You currently do not have any bills")
            }
        } else {
            bills.forEach { bill ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { navController.navigate("bill/${bill.id}") }
                        ) {
                            Text(bill.restaurantName, style = MaterialTheme.typography.titleLarge)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Total: $${bill.totalAmount}")
                            Text(
                                "Date: ${
                                    java.text.SimpleDateFormat("yyyy-MM-dd")
                                        .format(java.util.Date(bill.date))
                                }"
                            )
                        }

                        TextButton(
                            onClick = { viewModel.deleteBill(bill) }
                        ) {
                            Text("Delete", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BillDetailsScreen(
    bill: Bill,
    onPaymentToggle: (Int) -> Unit,
    onSave: (List<UserPayment>) -> Unit,
    navController: NavController
) {
    var localPayments by remember { mutableStateOf(bill.payments.map { it.copy() }) }

    val hasChanges = localPayments != bill.payments

    fun backToBills() {
        navController.navigate("bills") {
            popUpTo("bills") { inclusive = true }
            launchSingleTop = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextButton(onClick = { backToBills() }) {
            Text("← Back to Bills")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(bill.restaurantName, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Total: $${bill.totalAmount}")
        Text("Date: ${java.text.SimpleDateFormat("yyyy-MM-dd").format(Date(bill.date))}")
        Text("Location: Latitude %.4f, Longitude %.4f".format(bill.latitude, bill.longitude))

        Spacer(modifier = Modifier.height(8.dp))

        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(
                LatLng(bill.latitude, bill.longitude), 15f
            )
        }

        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(zoomControlsEnabled = false),
            properties = MapProperties(isMyLocationEnabled = false)
        ) {
            Marker(
                state = MarkerState(position = LatLng(bill.latitude, bill.longitude)),
                title = "Bill Location"
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("People:", style = MaterialTheme.typography.titleMedium)

        localPayments.forEachIndexed { index, payment ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${payment.name} - $${payment.amount}")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(if (payment.isPaid) "Paid" else "Unpaid")
                    Checkbox(
                        checked = payment.isPaid,
                        onCheckedChange = {
                            localPayments = localPayments.toMutableList().also {
                                it[index] = it[index].copy(isPaid = !it[index].isPaid)
                            }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                onSave(localPayments)

                backToBills()
            },
            enabled = hasChanges,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Save")
        }
    }
}

@Composable
fun CreateBillScreen(
    navController: NavController,
    billViewModel: BillViewModel,
    latitude: Double,
    longitude: Double
) {
    var restaurantName by remember { mutableStateOf("") }
    var totalAmount by remember { mutableStateOf("") }
    val users = remember { mutableStateListOf<UserPayment>() }
    var newUserName by remember { mutableStateOf("") }
    var newUserAmount by remember { mutableStateOf("") }

    val parsedTotalAmount = totalAmount.toDoubleOrNull() ?: 0.0
    val currentTotalOwed = users.sumOf { it.amount }
    val amountRemaining = parsedTotalAmount - currentTotalOwed

    val isFormValid = restaurantName.isNotBlank() &&
            totalAmount.toDoubleOrNull() != null &&
            users.isNotEmpty() &&
            amountRemaining == 0.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Create New Bill", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = restaurantName,
            onValueChange = { restaurantName = it },
            label = { Text("Restaurant Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = totalAmount,
            onValueChange = { totalAmount = it },
            label = { Text("Total Amount") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text("Add People to Bill", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(
            value = newUserName,
            onValueChange = { newUserName = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = newUserAmount,
            onValueChange = { newUserAmount = it },
            label = { Text("Amount Owed") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Button(
            onClick = {
                val amount = newUserAmount.toDoubleOrNull()
                if (newUserName.isNotBlank() && amount != null) {
                    users.add(
                        UserPayment(
                            name = newUserName,
                            amount = amount,
                            isPaid = false,
                            billId = 1
                        )
                    )

                    newUserName = ""
                    newUserAmount = ""
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Add Person")
        }

        Spacer(modifier = Modifier.height(16.dp))

        users.forEachIndexed { index, user ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${index + 1}. ${user.name} - $${user.amount} (${if (user.isPaid) "Paid" else "Unpaid"})")
                TextButton(onClick = {
                    if (index in users.indices) users.removeAt(index)
                }) {
                    Text("Remove")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Amount Remaining: $${"%.2f".format(amountRemaining)}", color = if (amountRemaining != 0.0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)

        if (amountRemaining != 0.0) {
            Text("Total owed must match bill amount", color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val bill = Bill(
                    restaurantName = restaurantName,
                    totalAmount = parsedTotalAmount,
                    date = Date().time,
                    longitude = longitude,
                    latitude = latitude
                )

                billViewModel.addBillWithPayments(bill, users.toList())

                navController.popBackStack()
            },
            enabled = isFormValid,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Create Bill")
        }
    }
}