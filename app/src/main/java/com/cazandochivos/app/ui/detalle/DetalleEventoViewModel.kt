package com.cazandochivos.app.ui.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cazandochivos.app.data.ChivosRepository
import kotlinx.coroutines.launch

class DetalleEventoViewModel(
    private val repo: ChivosRepository,
    val id: String
) : ViewModel() {
    val eventoFlow = repo.evento(id)

    fun toggleFavorito(actual: Boolean) {
        viewModelScope.launch { repo.toggleEventoFavorito(id, !actual) }
    }
}
