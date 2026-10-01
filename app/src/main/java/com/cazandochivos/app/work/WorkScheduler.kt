package com.cazandochivos.app.work

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.cazandochivos.app.util.Fechas
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

object WorkScheduler {
    private const val TAG_REFRESH = "refresh_chivos"
    private const val TAG_NOTIF = "notif_diaria"

    fun programarTodo(context: Context) {
        val wm = WorkManager.getInstance(context)
        val refresh = PeriodicWorkRequestBuilder<RefreshWorker>(12, TimeUnit.HOURS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .addTag(TAG_REFRESH)
            .build()
        wm.enqueueUniquePeriodicWork(TAG_REFRESH, ExistingPeriodicWorkPolicy.KEEP, refresh)
        programarNotificacionDiaria(context)
    }

    /** Programa el aviso para las próximas 9:00am en hora de Costa Rica. */
    fun programarNotificacionDiaria(context: Context) {
        val ahora = LocalDateTime.now(Fechas.ZONA)
        var proxima = ahora.toLocalDate().atTime(9, 0)
        if (!proxima.isAfter(ahora)) proxima = proxima.plusDays(1)
        val delayMin = Duration.between(ahora, proxima).toMinutes().coerceAtLeast(1)
        val req = OneTimeWorkRequestBuilder<NotificacionWorker>()
            .setInitialDelay(delayMin, TimeUnit.MINUTES)
            .addTag(TAG_NOTIF)
            .build()
        WorkManager.getInstance(context)
            .enqueueUniqueWork(TAG_NOTIF, ExistingWorkPolicy.REPLACE, req)
    }

    fun refrescarAhora(context: Context) {
        val req = OneTimeWorkRequestBuilder<RefreshWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
        WorkManager.getInstance(context).enqueue(req)
    }
}
