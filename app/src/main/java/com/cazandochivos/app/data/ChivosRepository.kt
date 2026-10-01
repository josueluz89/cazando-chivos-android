package com.cazandochivos.app.data

import androidx.room.withTransaction
import com.cazandochivos.app.data.api.AppDataDto
import com.cazandochivos.app.data.api.ChivosApi
import com.cazandochivos.app.data.api.EventoDto
import com.cazandochivos.app.data.api.LocalDto
import com.cazandochivos.app.data.db.AppDatabase
import com.cazandochivos.app.data.db.EventoEntity
import com.cazandochivos.app.data.db.LocalEntity
import com.cazandochivos.app.data.db.MetaEntity
import com.cazandochivos.app.data.db.historialAJson
import com.cazandochivos.app.util.Fechas
import kotlinx.coroutines.flow.Flow

class ChivosRepository(
    private val api: ChivosApi,
    private val db: AppDatabase
) {
    val eventos: Flow<List<EventoEntity>> = db.eventoDao().observarTodos()
    val locales: Flow<List<LocalEntity>> = db.localDao().observarTodos()
    val meta: Flow<MetaEntity?> = db.metaDao().observar()
    val eventosFavoritos: Flow<List<EventoEntity>> = db.eventoDao().observarFavoritos()
    val localesFavoritos: Flow<List<LocalEntity>> = db.localDao().observarFavoritos()

    fun evento(id: String): Flow<EventoEntity?> = db.eventoDao().observarPorId(id)

    fun local(nombre: String): Flow<LocalEntity?> = db.localDao().observarPorNombre(nombre)

    suspend fun estaVacio(): Boolean = db.eventoDao().contar() == 0

    suspend fun refresh(): Result<String> = try {
        val dto = api.obtenerDatos()
        db.withTransaction {
            val telefonos = dto.locales.associate { it.nombre to (it.telefono.orEmpty()) }
            val favEventos = db.eventoDao().favoritosIds().toSet()
            val entidades = dto.eventos
                .filter { it.banda.isNotBlank() && it.bar.isNotBlank() && it.fecha.isNotBlank() }
                .map { it.aEntidad(favEventos.contains(idEvento(it)), telefonos[it.bar].orEmpty()) }
            db.eventoDao().borrarTodos()
            db.eventoDao().insertarTodos(entidades)

            val favLocales = db.localDao().favoritosNombres().toSet()
            val localesEnt = dto.locales
                .filter { it.nombre.isNotBlank() }
                .map { it.aEntidad(favLocales.contains(it.nombre)) }
            db.localDao().borrarTodos()
            db.localDao().insertarTodos(localesEnt)

            db.metaDao().guardar(
                MetaEntity(
                    actualizado = dto.actualizado,
                    zonasJson = historialAJson(dto.zonas),
                    ventanaDias = dto.ventanaDias
                )
            )
        }
        Result.success(dto.actualizado)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun toggleEventoFavorito(id: String, fav: Boolean) {
        db.eventoDao().setFavorito(id, fav)
    }

    suspend fun toggleLocalFavorito(nombre: String, fav: Boolean) {
        db.localDao().setFavorito(nombre, fav)
    }

    suspend fun eventosDeHoy(): List<EventoEntity> =
        db.eventoDao().porFecha(Fechas.hoy().toString())

    companion object {
        fun idEvento(dto: EventoDto): String =
            "${dto.bar.trim().lowercase()}|${dto.fecha}|${dto.banda.trim().lowercase()}"

        private fun EventoDto.aEntidad(favorito: Boolean, telefonoBar: String) = EventoEntity(
            id = idEvento(this),
            banda = banda,
            bar = bar,
            fecha = fecha,
            hora = hora.ifBlank { "Por confirmar" },
            cover = cover,
            flyer = resolverFlyer(flyer),
            direccion = direccion,
            region = region,
            fuente = fuente,
            telefono = (telefono ?: "").ifBlank { telefonoBar },
            favorito = favorito
        )

        private fun LocalDto.aEntidad(favorito: Boolean) = LocalEntity(
            nombre = nombre,
            direccion = direccion,
            estilo = estilo,
            telefono = telefono ?: "",
            historialJson = historialAJson(historial),
            favorito = favorito
        )

        /** Los flyers vienen como ruta relativa ("flyers/x.jpg"); se resuelven a URL absoluta. */
        private fun resolverFlyer(flyer: String): String {
            val f = flyer.trim()
            if (f.isBlank()) return ""
            if (f.startsWith("http://") || f.startsWith("https://")) return f
            return "https://josueluz89.github.io/cazando-chivos/${f.trimStart('/')}"
        }
    }
}
