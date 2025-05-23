package com.example.billsplitting.ui.navigation

import Bill
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.billsplitting.ui.components.Screen
import com.example.billsplitting.ui.screen.BillDetailsScreen
import com.example.billsplitting.ui.screen.BillScreen
import com.example.billsplitting.ui.screen.CreateBillScreen
import com.example.billsplitting.ui.screen.HomeScreen
import com.example.billsplitting.ui.screen.MapScreen
import com.example.billsplitting.ui.screen.SettingsScreen
import com.example.billsplitting.ui.viewmodel.BillViewModel

@Composable
fun NavigationGraph(navController: NavHostController, modifier: Modifier, darkTheme: Boolean, onToggleTheme: (Boolean) -> Unit, billViewModel: BillViewModel) {
    NavHost(navController, startDestination = Screen.Home.route, modifier = modifier) {
        composable(Screen.Home.route) {
            HomeScreen(
                onMapClick = { navController.navigate(Screen.Map.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(Screen.Map.route) {
            MapScreen(navController)
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                darkTheme = darkTheme,
                onToggleTheme = onToggleTheme
            )
        }

        composable("bills") {
            BillScreen(navController, billViewModel)
        }

        composable("bills/create?lat={lat}&lng={lng}") { backStackEntry ->
            val lat = backStackEntry.arguments!!.getString("lat")!!.toDouble()
            val lng = backStackEntry.arguments!!.getString("lng")!!.toDouble()

            CreateBillScreen(navController, billViewModel, lat, lng)
        }

        composable("bill/{billId}") { backStackEntry ->
            val billId = backStackEntry.arguments?.getString("billId")?.toIntOrNull()

            var bill by remember { mutableStateOf<Bill?>(null) }

            LaunchedEffect(billId) {
                if (billId != null) {
                    bill = billViewModel.getBillById(billId)
                }
            }

            bill?.let {
                BillDetailsScreen(
                    bill = it,
                    onPaymentToggle = { index -> billViewModel.togglePayment(it.id, index) },
                    onSave = { updatedPayments -> billViewModel.updatePayments(it.id, updatedPayments) },
                    navController = navController
                )
            }
        }
    }
}