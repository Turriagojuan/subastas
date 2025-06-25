package com.example.subastas.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.subastas.ui.view.CreateAuctionScreen
import com.example.subastas.ui.view.DetailScreen
import com.example.subastas.ui.view.HomeScreen
import com.example.subastas.viewmodel.SubastaViewModel

// Qué hace: Configura la navegación de la aplicación usando Jetpack Navigation Compose.
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val subastaViewModel: SubastaViewModel = viewModel()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(navController = navController, viewModel = subastaViewModel)
        }
        composable("detail/{subastaId}") { backStackEntry ->
            val subastaId = backStackEntry.arguments?.getString("subastaId")?.toInt()
            requireNotNull(subastaId) { "El ID de la subasta no puede ser nulo" }
            DetailScreen(subastaId = subastaId, viewModel = subastaViewModel, navController = navController)
        }
        composable("create_auction") {
            CreateAuctionScreen(navController = navController, viewModel = subastaViewModel)
        }
    }
}