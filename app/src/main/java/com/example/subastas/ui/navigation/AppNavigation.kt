package com.example.subastas.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.subastas.ui.view.CreateAuctionScreen
import com.example.subastas.ui.view.DetailScreen
import com.example.subastas.ui.view.HomeScreen
import com.example.subastas.viewmodel.SubastaViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val subastaViewModel: SubastaViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(navController = navController, viewModel = subastaViewModel)
        }
        composable(
            route = "detail/{subastaId}",
            arguments = listOf(navArgument("subastaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val subastaId = backStackEntry.arguments?.getInt("subastaId")
            requireNotNull(subastaId) { "El ID de la subasta no puede ser nulo" }
            DetailScreen(
                subastaId = subastaId,
                viewModel = subastaViewModel,
                navController = navController
            )
        }
        composable("create_auction") {
            CreateAuctionScreen(navController = navController, viewModel = subastaViewModel)
        }
    }
}