package com.cazandochivos.app.ui.bares

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.cazandochivos.app.ui.components.BarRow
import com.cazandochivos.app.ui.components.TextoVacio

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaresScreen(
    vm: BaresViewModel,
    alAbrirBar: (String) -> Unit
) {
    val bares by vm.baresFlow.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bares del GAM") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (bares.isEmpty()) {
                item { TextoVacio("Todavía no hay bares cargados.\nAbrí la pestaña Chivos para descargar los datos.") }
            }
            items(bares, key = { it.local.nombre }) { b ->
                BarRow(
                    local = b.local,
                    proximo = b.proximo,
                    alClick = { alAbrirBar(b.local.nombre) },
                    alFavorito = { vm.toggleFavorito(b.local.nombre, !b.local.favorito) }
                )
            }
        }
    }
}
