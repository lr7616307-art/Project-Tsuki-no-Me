package me.tsukinome.project

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.util.concurrent.Executors

object PickedVideos {
    private val executor = Executors.newSingleThreadExecutor()

    fun import(
        context: Context,
        uris: List<Uri>,
        onDone: (added: Int, skipped: Int, failed: Int) -> Unit,
    ) {
        val app = context.applicationContext
        executor.execute {
            val settings = SettingsStore(app).load()
            val minMs = minOf(settings.minDurationMs, settings.maxDurationMs)
            val maxMs = maxOf(settings.minDurationMs, settings.maxDurationMs)
            var skipped = 0
            var failed = 0
            val added = ArrayList<VideoRef>()
            for (uri in uris) {
                try {
                    app.contentResolver.takePersistableUriPermission(
                        uri,
                        android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION,
                    )
                } catch (_: Exception) {
                }
                val duration = try {
                    VideoScanner.durationOf(app, uri)
                } catch (_: Exception) {
                    0L
                }
                if (duration <= 0L) {
                    failed++
                    continue
                }
                if (duration !in minMs..maxMs) {
                    skipped++
                    continue
                }
                added.add(
                    VideoRef(
                        uri = uri.toString(),
                        name = displayName(app, uri),
                        durationMs = duration,
                        location = "Selected",
                        picked = true,
                    ),
                )
            }
            if (added.isNotEmpty()) {
                val library = VideoLibrary(app)
                val merged = LinkedHashMap<String, VideoRef>()
                library.load().forEach { merged[it.uri] = it }
                added.forEach { merged[it.uri] = it }
                library.replace(merged.values.toList())
                SettingsStore(app).update {
                    it.copy(
                        hasScanned = true,
                        scanMode = if (it.scanMode.isBlank()) "picked" else it.scanMode,
                    )
                }
            }
            android.os.Handler(android.os.Looper.getMainLooper()).post {
                onDone(added.size, skipped, failed)
            }
        }
    }

    fun dropMissing(context: Context, onDone: (kept: Int, removed: Int) -> Unit) {
        val app = context.applicationContext
        executor.execute {
            val library = VideoLibrary(app)
            val current = library.load()
            val kept = current.filter { canOpen(app, it.uri) }
            library.replace(kept)
            android.os.Handler(android.os.Looper.getMainLooper()).post {
                onDone(kept.size, current.size - kept.size)
            }
        }
    }

    private fun canOpen(context: Context, uri: String): Boolean {
        return try {
            context.contentResolver.openAssetFileDescriptor(Uri.parse(uri), "r")?.use { true } ?: false
        } catch (_: Exception) {
            false
        }
    }

    private fun displayName(context: Context, uri: Uri): String {
        try {
            context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val name = cursor.getString(0)
                    if (!name.isNullOrBlank()) return name
                }
            }
        } catch (_: Exception) {
        }
        return "Video"
    }
}
