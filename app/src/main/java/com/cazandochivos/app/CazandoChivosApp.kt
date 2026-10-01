package com.cazandochivos.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.cazandochivos.app.data.ChivosRepository
import com.cazandochivos.app.data.api.ChivosApi
import com.cazandochivos.app.data.db.AppDatabase
import com.cazandochivos.app.work.WorkScheduler

class CazandoChivosApp : Application() {

    lateinit var repositorio: ChivosRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instancia = this
        val db = AppDatabase.crear(this)
        repositorio = ChivosRepository(ChivosApi.crear(), db)
        crearCanalNotificaciones()
        WorkScheduler.programarTodo(this)
    }

    private fun crearCanalNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CANAL_ID,
                getString(R.string.canal_chivos_nombre),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = getString(R.string.canal_chivos_desc)
            }
            getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
        }
    }

    companion object {
        const val CANAL_ID = "chivos_hoy"
        lateinit var instancia: CazandoChivosApp
            private set
    }
}
