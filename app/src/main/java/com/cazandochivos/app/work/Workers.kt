package com.cazandochivos.app.work

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.cazandochivos.app.CazandoChivosApp
import com.cazandochivos.app.R

/** Refresca la cartelera desde GitHub Pages (cada 12h + pull-to-refresh). */
class RefreshWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val repo = CazandoChivosApp.instancia.repositorio
        return repo.refresh().fold(
            onSuccess = { Result.success() },
            onFailure = { if (runAttemptCount < 3) Result.retry() else Result.failure() }
        )
    }
}

/** Avisa a las ~9am (hora CR) si hoy hay chivos. Se reprograma solo al terminar. */
class NotificacionWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        try {
            val eventos = CazandoChivosApp.instancia.repositorio.eventosDeHoy()
            if (eventos.isNotEmpty()) {
                mostrarNotificacion(eventos.map { "${it.banda} @ ${it.bar} (${it.hora})" })
            }
        } finally {
            WorkScheduler.programarNotificacionDiaria(applicationContext)
        }
        return Result.success()
    }

    private fun mostrarNotificacion(lineas: List<String>) {
        if (Build.VERSION.SDK_INT >= 33 &&
            ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) return
        val texto = lineas.take(4).joinToString("\n")
        val notificacion = NotificationCompat.Builder(applicationContext, CazandoChivosApp.CANAL_ID)
            .setSmallIcon(R.drawable.ic_stat_chivo)
            .setContentTitle("¡Hoy hay chivo! (${lineas.size})")
            .setContentText(lineas.first())
            .setStyle(NotificationCompat.BigTextStyle().bigText(texto))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(applicationContext).notify(1001, notificacion)
    }
}
