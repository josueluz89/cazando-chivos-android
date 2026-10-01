package com.cazandochivos.app.ui.chivos

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cazandochivos.app.R
import com.cazandochivos.app.ui.components.EventoCard
import com.cazandochivos.app.ui.components.SeccionTitulo
import com.cazandochivos.app.util.Fechas
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChivosScreen(
    vm: ChivosViewModel,
    alAbrirEvento: (String) -> Unit
) {
    val eventos by vm.eventosFlow.collectAsState(initial = emptyList())
    val zonas by vm.zonasFlow.collectAsState(initial = ChivosViewModel.ZONAS_DEFECTO)
    val meta by vm.metaFlow.collectAsState(initial = null)
    val hoy = Fechas.hoy().toString()

    val filtrados = eventos
        .filter { e ->
            (vm.zona == ChivosViewModel.TODO_GAM || e.region == vm.zona) &&
                e.fecha >= hoy &&
                (vm.busqueda.isBlank() ||
                    e.banda.contains(vm.busqueda, ignoreCase = true) ||
                    e.bar.contains(vm.busqueda, ignoreCase = true))
        }
        .sortedBy { it.fecha }

    val deHoy = filtrados.filter { it.fecha == hoy }
    val finde = filtrados.filter { it.fecha != hoy && Fechas.esEsteFinde(it.fecha) }
    val proximos = filtrados.filter { it.fecha != hoy && !Fechas.esEsteFinde(it.fecha) }
    val actualizado = meta?.actualizado?.let { Fechas.actualizadoCorto(it) }.orEmpty()

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2.357f)
            ) {
                Image(
                    painter = painterResource(R.drawable.banner_cazador),
                    contentDescription = "Cazador de Chivos",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )
                IconButton(
                    onClick = { vm.refresh() },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.55f),
                            CircleShape
                        )
                ) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Actualizar cartelera")
                }
            }
        }
    ) { padding ->
        SwipeRefresh(
            state = rememberSwipeRefreshState(vm.refrescando),
            onRefresh = { vm.refresh() },
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    OutlinedTextField(
                        value = vm.busqueda,
                        onValueChange = vm::cambiarBusqueda,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        placeholder = { Text("Buscar banda o bar…") },
                        leadingIcon = {
                            Icon(Icons.Filled.Search, contentDescription = null)
                        },
                        singleLine = true
                    )
                }
                item {
                    ZonaChips(
                        zonas = zonas,
                        seleccion = vm.zona,
                        alElegir = vm::cambiarZona
                    )
                }
                if (deHoy.isNotEmpty()) {
                    item { SeccionTitulo("¡Hoy hay chivo!") }
                    items(deHoy, key = { it.id }) { e ->
                        EventoCard(
                            evento = e,
                            alClick = { alAbrirEvento(e.id) },
                            alFavorito = { vm.toggleFavorito(e.id, !e.favorito) }
                        )
                    }
                }
                if (finde.isNotEmpty()) {
                    item { SeccionTitulo("Este finde") }
                    items(finde, key = { it.id }) { e ->
                        EventoCard(
                            evento = e,
                            alClick = { alAbrirEvento(e.id) },
                            alFavorito = { vm.toggleFavorito(e.id, !e.favorito) }
                        )
                    }
                }
                if (proximos.isNotEmpty()) {
                    item { SeccionTitulo("Próximos") }
                    items(proximos, key = { it.id }) { e ->
                        EventoCard(
                            evento = e,
                            alClick = { alAbrirEvento(e.id) },
                            alFavorito = { vm.toggleFavorito(e.id, !e.favorito) }
                        )
                    }
                }
                if (filtrados.isEmpty()) {
                    item {
                        Text(
                            text = "No hay chivos con ese filtro.\nDeslizá hacia abajo para actualizar.",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (actualizado.isNotEmpty()) {
                    item {
                        Text(
                            text = "Actualizado: $actualizado",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ZonaChips(
    zonas: List<String>,
    seleccion: String,
    alElegir: (String) -> Unit
) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        zonas.forEach { z ->
            FilterChip(
                selected = seleccion == z,
                onClick = { alElegir(z) },
                label = { Text(z) }
            )
        }
    }
}
