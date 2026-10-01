package com.cazandochivos.app.ui.detalle

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.cazandochivos.app.R
import com.cazandochivos.app.data.db.EventoEntity
import com.cazandochivos.app.ui.theme.WhatsApp
import com.cazandochivos.app.ui.theme.WhatsAppTinta
import com.cazandochivos.app.util.Fechas
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleEventoScreen(
    vm: DetalleEventoViewModel,
    alAtras: () -> Unit,
    alAbrirBar: (String) -> Unit
) {
    val evento by vm.eventoFlow.collectAsState(initial = null)
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chivo") },
                navigationIcon = {
                    IconButton(onClick = alAtras) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    evento?.let { e ->
                        IconButton(onClick = { vm.toggleFavorito(e.favorito) }) {
                            Icon(
                                imageVector = if (e.favorito) Icons.Filled.Favorite
                                else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Guardar en favoritos",
                                tint = if (e.favorito) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        val e = evento
        if (e == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                item {
                    if (e.flyer.isNotBlank()) {
                        AsyncImage(
                            model = e.flyer,
                            contentDescription = "Flyer de ${e.banda}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.FillWidth
                        )
                        Spacer(Modifier.height(16.dp))
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_ticket),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(44.dp)
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = e.bar,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                    }
                    if (Fechas.esHoy(e.fecha)) {
                        Text(
                            text = "¡HOY!",
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.ExtraBold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(4.dp))
                    }
                    Text(
                        text = e.banda,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.ic_clock),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = " ${Fechas.larga(e.fecha)} · ${e.hora}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { alAbrirBar(e.bar) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_pin),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Column(modifier = Modifier.padding(start = 8.dp)) {
                            Text(
                                text = e.bar,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            if (e.direccion.isNotBlank()) {
                                Text(
                                    text = e.direccion,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.ic_ticket),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = " Cover: ${e.cover.ifBlank { "Por confirmar" }}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Spacer(Modifier.height(20.dp))
                    Button(
                        onClick = { comoLlegar(context, e) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.Directions, contentDescription = null)
                        Text("  Cómo llegar", modifier = Modifier.padding(start = 4.dp))
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { compartir(context, e) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Filled.Share, contentDescription = null)
                            Text("  Compartir", modifier = Modifier.padding(start = 4.dp))
                        }
                        OutlinedButton(
                            onClick = { alCalendario(context, e) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Filled.CalendarMonth, contentDescription = null)
                            Text("  Al calendario", modifier = Modifier.padding(start = 4.dp))
                        }
                    }
                    numeroWhatsApp(e.telefono)?.let { num ->
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = { abrirWhatsApp(context, num, e.banda) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = WhatsApp,
                                contentColor = WhatsAppTinta
                            )
                        ) {
                            Text("WhatsApp del bar", fontWeight = FontWeight.Bold)
                        }
                    }
                    if (e.fuente.isNotBlank()) {
                        Spacer(Modifier.height(8.dp))
                        TextButton(
                            onClick = {
                                context.startActivity(
                                    Intent(Intent.ACTION_VIEW, Uri.parse(e.fuente))
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Ver publicación original")
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}

private fun comoLlegar(context: Context, e: EventoEntity) {
    val q = Uri.encode("${e.bar}, ${e.direccion}, Costa Rica")
    val geo = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$q"))
    try {
        context.startActivity(geo)
    } catch (_: Exception) {
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/maps/search/?api=1&query=$q")
            )
        )
    }
}

private fun compartir(context: Context, e: EventoEntity) {
    val texto =
        "¡Chivo! ${e.banda} en ${e.bar} — ${Fechas.larga(e.fecha)}, ${e.hora}. ${e.fuente}".trim()
    val send = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, texto)
    }
    context.startActivity(Intent.createChooser(send, "Compartir chivo"))
}

private fun inicioMillis(e: EventoEntity): Long? = try {
    val horaBase = if (Regex("^\\d{1,2}:\\d{2}$").matches(e.hora)) e.hora else "20:00"
    LocalDateTime.parse("${e.fecha}T${horaBase.padStart(5, '0')}")
        .atZone(Fechas.ZONA)
        .toInstant()
        .toEpochMilli()
} catch (_: Exception) {
    null
}

private fun alCalendario(context: Context, e: EventoEntity) {
    val inicio = inicioMillis(e) ?: return
    val intent = Intent(Intent.ACTION_INSERT).apply {
        data = CalendarContract.Events.CONTENT_URI
        putExtra(CalendarContract.Events.TITLE, "Chivo: ${e.banda}")
        putExtra(CalendarContract.Events.EVENT_LOCATION, "${e.bar}, ${e.direccion}")
        putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, inicio)
        putExtra(CalendarContract.EXTRA_EVENT_END_TIME, inicio + 3 * 60 * 60 * 1000L)
    }
    context.startActivity(intent)
}

/** Normaliza a formato wa.me con código país 506. Null si no hay número válido. */
private fun numeroWhatsApp(telefono: String): String? {
    val digitos = telefono.filter { it.isDigit() }
    if (digitos.length < 8) return null
    return if (digitos.startsWith("506")) digitos else "506$digitos"
}

private fun abrirWhatsApp(context: Context, numero: String, banda: String) {
    val texto = Uri.encode("Hola, ¿me dan info del chivo de $banda por favor?")
    context.startActivity(
        Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$numero?text=$texto"))
    )
}
