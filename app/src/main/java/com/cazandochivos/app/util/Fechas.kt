package com.cazandochivos.app.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object Fechas {
    val ZONA: ZoneId = ZoneId.of("America/Costa_Rica")
    private val LOCALE_CR = Locale("es", "CR")
    private val FORMATO_CORTO = DateTimeFormatter.ofPattern("EEE d MMM", LOCALE_CR)
    private val FORMATO_LARGO = DateTimeFormatter.ofPattern("EEEE d 'de' MMMM yyyy", LOCALE_CR)
    private val FORMATO_ACTUALIZADO = DateTimeFormatter.ofPattern("d MMM HH:mm", LOCALE_CR)

    fun hoy(): LocalDate = LocalDate.now(ZONA)

    fun parse(fecha: String): LocalDate? = try {
        LocalDate.parse(fecha, DateTimeFormatter.ISO_LOCAL_DATE)
    } catch (_: Exception) {
        null
    }

    fun esHoy(fecha: String): Boolean = parse(fecha) == hoy()

    fun esEsteFinde(fecha: String): Boolean {
        val f = parse(fecha) ?: return false
        val h = hoy()
        if (f.isBefore(h) || f.isAfter(h.plusDays(7))) return false
        return f.dayOfWeek == DayOfWeek.SATURDAY || f.dayOfWeek == DayOfWeek.SUNDAY
    }

    /** "vie 2 oct" */
    fun corta(fecha: String): String {
        val f = parse(fecha) ?: return fecha
        return FORMATO_CORTO.format(f).replace(".", "")
    }

    /** "viernes 2 de octubre 2026" */
    fun larga(fecha: String): String {
        val f = parse(fecha) ?: return fecha
        return FORMATO_LARGO.format(f).replace(".", "")
    }

    /** "1 oct 01:48" a partir de ISO con offset */
    fun actualizadoCorto(iso: String): String = try {
        FORMATO_ACTUALIZADO.format(OffsetDateTime.parse(iso)).replace(".", "")
    } catch (_: Exception) {
        ""
    }
}
