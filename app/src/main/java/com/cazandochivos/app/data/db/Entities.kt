package com.cazandochivos.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

private val gsonJson = Gson()

fun historialAJson(lista: List<String>): String = gsonJson.toJson(lista)

fun historialDesdeJson(json: String): List<String> = try {
    gsonJson.fromJson<List<String>>(json, object : TypeToken<List<String>>() {}.type)
        ?: emptyList()
} catch (_: Exception) {
    emptyList()
}

@Entity(tableName = "eventos")
data class EventoEntity(
    @PrimaryKey val id: String,
    val banda: String,
    val bar: String,
    val fecha: String,
    val hora: String,
    val cover: String,
    val flyer: String,
    val direccion: String,
    val region: String,
    val fuente: String,
    val telefono: String,
    val favorito: Boolean = false
)

@Entity(tableName = "locales")
data class LocalEntity(
    @PrimaryKey val nombre: String,
    val direccion: String,
    val estilo: String,
    val telefono: String,
    val historialJson: String,
    val favorito: Boolean = false
) {
    val historial: List<String>
        get() = historialDesdeJson(historialJson)
}

@Entity(tableName = "meta")
data class MetaEntity(
    @PrimaryKey val id: Int = 1,
    val actualizado: String = "",
    val zonasJson: String = "[]",
    val ventanaDias: Int = 60
)
