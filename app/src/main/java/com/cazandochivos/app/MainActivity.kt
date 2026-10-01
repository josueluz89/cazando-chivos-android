package com.cazandochivos.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.cazandochivos.app.ui.nav.AppNav
import com.cazandochivos.app.ui.theme.CazandoChivosTheme
import com.cazandochivos.app.work.WorkScheduler

class MainActivity : ComponentActivity() {

    private val pedirNotificaciones =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            pedirNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        // Primera sincronización inmediata; luego cada 12h con WorkManager.
        WorkScheduler.refrescarAhora(this)

        setContent {
            CazandoChivosTheme {
                AppNav()
            }
        }
    }
}
