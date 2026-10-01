package com.cazandochivos.app.ui.nav

import android.util.Base64

object Destinos {
    const val CHIVOS = "chivos"
    const val BARES = "bares"
    const val FAVORITOS = "favoritos"
    const val DETALLE_EVENTO = "evento/{id}"
    const val DETALLE_BAR = "bar/{id}"

    fun evento(id: String): String = "evento/${aB64(id)}"
    fun bar(nombre: String): String = "bar/${aB64(nombre)}"

    /** Base64 URL-safe: sin caracteres problemáticos para la ruta de navegación. */
    fun aB64(texto: String): String = Base64.encodeToString(
        texto.toByteArray(Charsets.UTF_8),
        Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
    )

    fun deB64(b64: String): String = try {
        Base64.decode(b64, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
            .toString(Charsets.UTF_8)
    } catch (_: Exception) {
        ""
    }
}
