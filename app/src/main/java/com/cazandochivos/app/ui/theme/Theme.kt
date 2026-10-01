package com.cazandochivos.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val EsquemaOscuro = darkColorScheme(
    primary = Rojo,
    onPrimary = Tinta,
    secondary = Dorado,
    onSecondary = Fondo,
    tertiary = Dorado,
    onTertiary = Fondo,
    background = Fondo,
    onBackground = Tinta,
    surface = Superficie,
    onSurface = Tinta,
    surfaceVariant = SuperficieAlta,
    onSurfaceVariant = TintaApagada,
    outline = Linea,
    error = Rojo,
    onError = Tinta
)

@Composable
fun CazandoChivosTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EsquemaOscuro,
        content = content
    )
}
