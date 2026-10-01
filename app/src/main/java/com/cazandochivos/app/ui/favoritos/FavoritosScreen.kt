package com.cazandochivos.app.ui.favoritos

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
import com.cazandochivos.app.ui.components.EventoCard
import com.cazandochivos.app.ui.components.SeccionTitulo
import com.cazandochivos.app.ui.components.TextoVacio

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritosScreen(
    vm: FavoritosViewModel,
    alAbrirEvento: (String) -> Unit,
    alAbrirBar: (String) -> Unit
) {
    val eventos by vm.eventosFlow.collectAsState(initial = emptyList())
    val bares by vm.baresFlow.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favoritos") },
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
            item { SeccionTitulo("Chivos guardados") }
            if (eventos.isEmpty()) {
                item { TextoVacio("Tocá el corazón en un chivo para guardarlo aquí.") }
            }
            items(eventos, key = { it.id }) { e ->
                EventoCard(
                    evento = e,
                    alClick = { alAbrirEvento(e.id) },
                    alFavorito = { vm.toggleEvento(e.id, !e.favorito) }
                )
            }
            item { SeccionTitulo("Bares guardados") }
            if (bares.isEmpty()) {
                item { TextoVacio("Tocá el corazón en un bar para guardarlo aquí.") }
            }
            items(bares, key = { it.local.nombre }) { b ->
                BarRow(
                    local = b.local,
                    proximo = b.proximo,
                    alClick = { alAbrirBar(b.local.nombre) },
                    alFavorito = { vm.toggleBar(b.local.nombre, !b.local.favorito) }
                )
            }
        }
    }
}
