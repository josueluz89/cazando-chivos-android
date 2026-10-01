package com.cazandochivos.app.ui.nav

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cazandochivos.app.CazandoChivosApp
import com.cazandochivos.app.ui.VmFactory
import com.cazandochivos.app.ui.bares.BaresScreen
import com.cazandochivos.app.ui.bares.BaresViewModel
import com.cazandochivos.app.ui.bares.DetalleBarScreen
import com.cazandochivos.app.ui.bares.DetalleBarViewModel
import com.cazandochivos.app.ui.chivos.ChivosScreen
import com.cazandochivos.app.ui.chivos.ChivosViewModel
import com.cazandochivos.app.ui.detalle.DetalleEventoScreen
import com.cazandochivos.app.ui.detalle.DetalleEventoViewModel
import com.cazandochivos.app.ui.favoritos.FavoritosScreen
import com.cazandochivos.app.ui.favoritos.FavoritosViewModel

@Composable
fun AppNav() {
    val navController = rememberNavController()
    val app = LocalContext.current.applicationContext as CazandoChivosApp
    val repo = app.repositorio
    val entrada by navController.currentBackStackEntryAsState()
    val ruta = entrada?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = ruta == Destinos.CHIVOS,
                    onClick = {
                        navController.navigate(Destinos.CHIVOS) {
                            popUpTo(Destinos.CHIVOS)
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.Filled.MusicNote, contentDescription = null) },
                    label = { Text("Chivos") }
                )
                NavigationBarItem(
                    selected = ruta == Destinos.BARES,
                    onClick = {
                        navController.navigate(Destinos.BARES) {
                            popUpTo(Destinos.CHIVOS)
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.Filled.Store, contentDescription = null) },
                    label = { Text("Bares") }
                )
                NavigationBarItem(
                    selected = ruta == Destinos.FAVORITOS,
                    onClick = {
                        navController.navigate(Destinos.FAVORITOS) {
                            popUpTo(Destinos.CHIVOS)
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
                    label = { Text("Favoritos") }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Destinos.CHIVOS,
            modifier = Modifier.padding(padding)
        ) {
            composable(Destinos.CHIVOS) {
                ChivosScreen(
                    vm = viewModel(factory = VmFactory { ChivosViewModel(repo) }),
                    alAbrirEvento = { id -> navController.navigate(Destinos.evento(id)) }
                )
            }
            composable(Destinos.BARES) {
                BaresScreen(
                    vm = viewModel(factory = VmFactory { BaresViewModel(repo) }),
                    alAbrirBar = { nombre -> navController.navigate(Destinos.bar(nombre)) }
                )
            }
            composable(Destinos.FAVORITOS) {
                FavoritosScreen(
                    vm = viewModel(factory = VmFactory { FavoritosViewModel(repo) }),
                    alAbrirEvento = { id -> navController.navigate(Destinos.evento(id)) },
                    alAbrirBar = { nombre -> navController.navigate(Destinos.bar(nombre)) }
                )
            }
            composable(
                route = Destinos.DETALLE_EVENTO,
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { entry ->
                val id = Destinos.deB64(entry.arguments?.getString("id").orEmpty())
                DetalleEventoScreen(
                    vm = viewModel(factory = VmFactory { DetalleEventoViewModel(repo, id) }),
                    alAtras = { navController.popBackStack() },
                    alAbrirBar = { nombre -> navController.navigate(Destinos.bar(nombre)) }
                )
            }
            composable(
                route = Destinos.DETALLE_BAR,
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { entry ->
                val nombre = Destinos.deB64(entry.arguments?.getString("id").orEmpty())
                DetalleBarScreen(
                    vm = viewModel(factory = VmFactory { DetalleBarViewModel(repo, nombre) }),
                    alAtras = { navController.popBackStack() },
                    alAbrirEvento = { id -> navController.navigate(Destinos.evento(id)) }
                )
            }
        }
    }
}
