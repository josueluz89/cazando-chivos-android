package com.cazandochivos.app.ui.bares

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cazandochivos.app.data.ChivosRepository
import com.cazandochivos.app.data.db.EventoEntity
import com.cazandochivos.app.data.db.LocalEntity
import com.cazandochivos.app.util.Fechas
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class BarConProximo(
    val local: LocalEntity,
    val proximo: EventoEntity?
)

class BaresViewModel(private val repo: ChivosRepository) : ViewModel() {

    val baresFlow: Flow<List<BarConProximo>> =
        combine(repo.eventos, repo.locales) { eventos, locales ->
            val hoy = Fechas.hoy().toString()
            locales.map { l ->
                val prox = eventos
                    .filter { it.bar == l.nombre && it.fecha >= hoy }
                    .minByOrNull { it.fecha }
                BarConProximo(l, prox)
            }.sortedWith(
                compareBy<BarConProximo> { it.proximo == null }
                    .thenBy { it.proximo?.fecha ?: "9999" }
            )
        }

    fun toggleFavorito(nombre: String, fav: Boolean) {
        viewModelScope.launch { repo.toggleLocalFavorito(nombre, fav) }
    }
}
