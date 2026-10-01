package com.cazandochivos.app.ui.chivos

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cazandochivos.app.data.ChivosRepository
import com.cazandochivos.app.data.db.historialDesdeJson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ChivosViewModel(private val repo: ChivosRepository) : ViewModel() {

    val eventosFlow = repo.eventos
    val metaFlow = repo.meta

    val zonasFlow: Flow<List<String>> = repo.meta.map { m ->
        val zs = m?.zonasJson?.let { historialDesdeJson(it) }.orEmpty()
        if (zs.isEmpty()) ZONAS_DEFECTO else listOf(TODO_GAM) + zs.filter { it != TODO_GAM }
    }

    var zona by mutableStateOf(TODO_GAM)
        private set
    var busqueda by mutableStateOf("")
        private set
    var refrescando by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            if (repo.estaVacio()) refresh()
        }
    }

    fun cambiarZona(z: String) {
        zona = z
    }

    fun cambiarBusqueda(q: String) {
        busqueda = q
    }

    fun refresh() {
        viewModelScope.launch {
            refrescando = true
            repo.refresh()
            refrescando = false
        }
    }

    fun toggleFavorito(id: String, fav: Boolean) {
        viewModelScope.launch { repo.toggleEventoFavorito(id, fav) }
    }

    companion object {
        const val TODO_GAM = "Todo el GAM"
        val ZONAS_DEFECTO = listOf(TODO_GAM, "Alajuela", "Cartago", "Heredia", "San José")
    }
}
