package com.cazandochivos.app.data.api

import com.google.gson.annotations.SerializedName

data class AppDataDto(
    @SerializedName("actualizado") val actualizado: String = "",
    @SerializedName("ventana_dias") val ventanaDias: Int = 60,
    @SerializedName("zonas") val zonas: List<String> = emptyList(),
    @SerializedName("eventos") val eventos: List<EventoDto> = emptyList(),
    @SerializedName("locales") val locales: List<LocalDto> = emptyList()
)

data class EventoDto(
    @SerializedName("banda") val banda: String = "",
    @SerializedName("bar") val bar: String = "",
    @SerializedName("fecha") val fecha: String = "",
    @SerializedName("hora") val hora: String = "",
    @SerializedName("cover") val cover: String = "",
    @SerializedName("flyer") val flyer: String = "",
    @SerializedName("direccion_display") val direccion: String = "",
    @SerializedName("region") val region: String = "",
    @SerializedName("fuente") val fuente: String = "",
    @SerializedName("telefono") val telefono: String? = null
)

data class LocalDto(
    @SerializedName("nombre") val nombre: String = "",
    @SerializedName("direccion") val direccion: String = "",
    @SerializedName("estilo") val estilo: String = "",
    @SerializedName("historial") val historial: List<String> = emptyList()
)
