package com.cazandochivos.app.ui.bares

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cazandochivos.app.ui.components.EventoCard
import com.cazandochivos.app.ui.components.SeccionTitulo
import com.cazandochivos.app.ui.components.TextoVacio

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DetalleBarScreen(
    vm: DetalleBarViewModel,
    alAtras: () -> Unit,
    alAbrirEvento: (String) -> Unit
) {
    val local by vm.localFlow.collectAsState(initial = null)
    val eventos by vm.eventosFlow.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(vm.nombre) },
                navigationIcon = {
                    IconButton(onClick = alAtras) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    local?.let { l ->
                        IconButton(onClick = { vm.toggleFavorito(l.favorito) }) {
                            Icon(
                                imageVector = if (l.favorito) Icons.Filled.Favorite
                                else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Guardar en favoritos",
                                tint = if (l.favorito) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        val l = local
        if (l == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = l.nombre,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        if (l.direccion.isNotBlank()) {
                            Text(
                                text = l.direccion,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (l.estilo.isNotBlank()) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = l.estilo,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
                item { SeccionTitulo("Próximos chivos") }
                if (eventos.isEmpty()) {
                    item { TextoVacio("Este bar no tiene chivos próximos.") }
                } else {
                    items(eventos, key = { it.id }) { e ->
                        EventoCard(
                            evento = e,
                            alClick = { alAbrirEvento(e.id) },
                            alFavorito = { vm.toggleEventoFavorito(e.id, !e.favorito) }
                        )
                    }
                }
                if (l.historial.isNotEmpty()) {
                    item { SeccionTitulo("Han tocado aquí") }
                    item {
                        FlowRow(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            l.historial.forEach { banda ->
                                AssistChip(
                                    onClick = { },
                                    label = { Text(banda) }
                                )
                            }
                        }
                        Spacer(Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}
