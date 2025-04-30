package com.example.billsplitting

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.billsplitting.ui.components.BottomNavigationBar
import com.example.billsplitting.ui.navigation.NavigationGraph
import com.example.billsplitting.ui.viewmodel.BillViewModel
import com.example.billsplitting.viewmodel.BillViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var darkTheme by remember { mutableStateOf(true) }

            MaterialTheme(
                colorScheme = if (darkTheme) darkColorScheme() else lightColorScheme()
            ) {
                MainScreen(
                    darkTheme = darkTheme,
                    onToggleTheme = { darkTheme = it }
                )
            }
        }
    }
}


@Composable
fun MainScreen(
    darkTheme: Boolean,
    onToggleTheme: (Boolean) -> Unit
) {
    val navController = rememberNavController()
    val context = LocalContext.current.applicationContext as Application
    val factory = remember { BillViewModelFactory(context) }
    val billViewModel: BillViewModel = viewModel(factory = factory)

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavigationGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            darkTheme = darkTheme,
            onToggleTheme = onToggleTheme,
            billViewModel = billViewModel
        )
    }
}

