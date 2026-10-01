package com.cazandochivos.app.ui.favoritos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cazandochivos.app.data.ChivosRepository
import com.cazandochivos.app.ui.bares.BarConProximo
import com.cazandochivos.app.util.Fechas
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class FavoritosViewModel(private val repo: ChivosRepository) : ViewModel() {

    val eventosFlow = repo.eventosFavoritos

    val baresFlow: Flow<List<BarConProximo>> =
        combine(repo.eventos, repo.localesFavoritos) { eventos, locales ->
            val hoy = Fechas.hoy().toString()
            locales.map { l ->
                val prox = eventos
                    .filter { it.bar == l.nombre && it.fecha >= hoy }
                    .minByOrNull { it.fecha }
                BarConProximo(l, prox)
            }
        }

    fun toggleEvento(id: String, fav: Boolean) {
        viewModelScope.launch { repo.toggleEventoFavorito(id, fav) }
    }

    fun toggleBar(nombre: String, fav: Boolean) {
        viewModelScope.launch { repo.toggleLocalFavorito(nombre, fav) }
    }
}
