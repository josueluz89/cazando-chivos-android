# Cazando Chivos — App Android

App nativa (Kotlin + Jetpack Compose + Material 3) para descubrir conciertos de
rock en vivo ("chivos") en el GAM, Costa Rica.

## De dónde salen los datos

La app **no tiene backend**. Lee un único JSON publicado en GitHub Pages:

```
https://josueluz89.github.io/cazando-chivos/data/app.json
```

Ese archivo lo genera automáticamente el pipeline de vigilancia del repo
`cazando-chivos` (búsquedas en Facebook 2 veces al día, 9am y 6pm hora de
Costa Rica). La app lo descarga, lo guarda en caché local (Room) y lo
refresca cada 12 horas con WorkManager, además de pull-to-refresh manual.

## Funcionalidades

- **Chivos**: secciones Hoy / Este finde / Próximos, chips de zona
  (Todo el GAM, Alajuela, Cartago, Heredia, San José), buscador y cards con
  flyer, fecha, banda, bar, hora y cover. ¡HOY! resaltado en dorado.
- **Detalle de evento**: flyer grande, fecha/hora, bar, dirección, cover y
  botones: Cómo llegar (Maps), Compartir, Agregar al calendario y WhatsApp
  del bar (si hay número).
- **Bares**: ordenados por próximo evento; indicador rojo si no tienen
  chivos próximos; detalle con próximos eventos y "Han tocado aquí".
- **Favoritos**: eventos y bares guardados en local (Room).
- **Offline primero**: todo funciona sin internet con la última cartelera
  descargada.
- **Notificación local**: todos los días ~9am (hora CR) avisa si "Hoy hay
  chivo" con los eventos del día. Pide el permiso de notificaciones al abrir.

## Compilar

Requisitos: Android Studio Ladybug o superior (o JDK 17 + Gradle 8.7).

```bash
# Con Android Studio: abrir el proyecto y correr en un emulador/dispositivo.

# Por línea de comandos (Gradle 8.7 instalado):
gradle :app:assembleDebug --no-daemon
# APK en: app/build/outputs/apk/debug/app-debug.apk
```

También hay un workflow de GitHub Actions (`.github/workflows/build-apk.yml`):
en cada push a `main` compila el APK debug y lo deja como artifact; en tags
`v*` (ej. `v1.0.0`) crea un GitHub Release con el APK listo para descargar.

## Estructura

```
app/src/main/java/com/cazandochivos/app/
├── MainActivity.kt            # Permiso notificaciones + refresh inicial
├── CazandoChivosApp.kt        # Application: DB, repo, canal, workers
├── data/
│   ├── api/                   # Retrofit → data/app.json (DTOs)
│   ├── db/                    # Room: eventos, locales, meta
│   └── ChivosRepository.kt    # Única fuente de verdad para la UI
├── ui/
│   ├── theme/                 # Tema oscuro rockero (#0D0B0C, #E02020, #F0B429)
│   ├── nav/                   # Bottom nav + destinos
│   ├── chivos/ / bares/ / favoritos/ / detalle/  # Pantallas + ViewModels
│   └── components/            # EventoCard, BarRow, etc.
├── work/                      # RefreshWorker (12h), NotificacionWorker (9am CR)
└── util/Fechas.kt             # Fechas en America/Costa_Rica
```

- `minSdk 26` · `targetSdk 34` · Sin Hilt (ViewModels manuales) · Sin secretos
  ni API keys: el único "servidor" es el JSON público de GitHub Pages.
