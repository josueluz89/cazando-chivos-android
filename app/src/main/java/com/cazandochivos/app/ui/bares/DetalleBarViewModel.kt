package com.cazandochivos.app.ui.bares

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cazandochivos.app.data.ChivosRepository
import com.cazandochivos.app.data.db.EventoEntity
import com.cazandochivos.app.util.Fechas
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class DetalleBarViewModel(
    private val repo: ChivosRepository,
    val nombre: String
) : ViewModel() {

    val localFlow = repo.local(nombre)

    val eventosFlow: Flow<List<EventoEntity>> =
        repo.eventos.map { eventos ->
            val hoy = Fechas.hoy().toString()
            eventos
                .filter { it.bar == nombre && it.fecha >= hoy }
                .sortedBy { it.fecha }
        }

    fun toggleFavorito(actual: Boolean) {
        viewModelScope.launch { repo.toggleLocalFavorito(nombre, !actual) }
    }

    fun toggleEventoFavorito(id: String, fav: Boolean) {
        viewModelScope.launch { repo.toggleEventoFavorito(id, fav) }
    }
}
