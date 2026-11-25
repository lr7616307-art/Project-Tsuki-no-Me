package me.tsukinome.project

import android.content.Context
import org.json.JSONArray

data class AppSettings(
    val windowStartMin: Int = 8 * 60,
    val windowEndMin: Int = 20 * 60,
    val videosPerHour: Int = 5,
    val pauseUntilEpoch: Long = 0L,
    val minDurationMs: Long = 3_000L,
    val maxDurationMs: Long = 120_000L,
    val soundOn: Boolean = true,
    val allowSkip: Boolean = false,
    val playOnHome: Boolean = true,
    val selectionMode: String = MODE_ONLY,
    val selectedPackages: Set<String> = emptySet(),
    val hasScanned: Boolean = false,
    val scanMode: String = "",
    val folderTreeUri: String = "",
    val playLog: List<Long> = emptyList(),
    val lastPlayedUri: String = "",
    val introSeen: Boolean = false,
    val callerVideoEnabled: Boolean = false,
    val callerVideoMode: String = MODE_RANDOM,
    val callerVideoUri: String = "",
    val callerSound: Boolean = true,
) {
    companion object {
        const val MODE_ONLY = "only"
        const val MODE_EXCEPT = "except"
        const val MODE_RANDOM = "random"
        const val MODE_SINGLE = "single"
    }
}

class SettingsStore(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun load(): AppSettings {
        val selected = prefs.getStringSet(KEY_PACKAGES, emptySet())?.toSet() ?: emptySet()
        val log = parseLog(prefs.getString(KEY_LOG, "") ?: "")
        return AppSettings(
            windowStartMin = prefs.getInt(KEY_START, 8 * 60),
            windowEndMin = prefs.getInt(KEY_END, 20 * 60),
            videosPerHour = prefs.getInt(KEY_PER_HOUR, 5).coerceIn(1, 20),
            pauseUntilEpoch = prefs.getLong(KEY_PAUSE, 0L),
            minDurationMs = prefs.getLong(KEY_MIN_MS, 3_000L).coerceAtLeast(1_000L),
            maxDurationMs = prefs.getLong(KEY_MAX_MS, 120_000L).coerceAtLeast(1_000L),
            soundOn = prefs.getBoolean(KEY_SOUND, true),
            allowSkip = prefs.getBoolean(KEY_SKIP, false),
            playOnHome = prefs.getBoolean(KEY_HOME, true),
            selectionMode = prefs.getString(KEY_MODE, AppSettings.MODE_ONLY) ?: AppSettings.MODE_ONLY,
            selectedPackages = selected,
            hasScanned = prefs.getBoolean(KEY_SCANNED, false),
            scanMode = prefs.getString(KEY_SCAN_MODE, "") ?: "",
            folderTreeUri = prefs.getString(KEY_FOLDER, "") ?: "",
            playLog = log,
            lastPlayedUri = prefs.getString(KEY_LAST, "") ?: "",
            introSeen = prefs.getBoolean(KEY_INTRO, false),
            callerVideoEnabled = prefs.getBoolean(KEY_CALLER, false),
            callerVideoMode = prefs.getString(KEY_CALLER_MODE, AppSettings.MODE_RANDOM) ?: AppSettings.MODE_RANDOM,
            callerVideoUri = prefs.getString(KEY_CALLER_URI, "") ?: "",
            callerSound = prefs.getBoolean(KEY_CALLER_SOUND, true),
        )
    }

    fun save(settings: AppSettings) {
        val safeMin = settings.minDurationMs.coerceAtLeast(1_000L)
        val safeMax = settings.maxDurationMs.coerceAtLeast(safeMin)
        prefs.edit()
            .putInt(KEY_START, settings.windowStartMin.coerceIn(0, 24 * 60 - 1))
            .putInt(KEY_END, settings.windowEndMin.coerceIn(0, 24 * 60 - 1))
            .putInt(KEY_PER_HOUR, settings.videosPerHour.coerceIn(1, 20))
            .putLong(KEY_PAUSE, settings.pauseUntilEpoch)
            .putLong(KEY_MIN_MS, safeMin)
            .putLong(KEY_MAX_MS, safeMax)
            .putBoolean(KEY_SOUND, settings.soundOn)
            .putBoolean(KEY_SKIP, settings.allowSkip)
            .putBoolean(KEY_HOME, settings.playOnHome)
            .putString(KEY_MODE, settings.selectionMode)
            .putStringSet(KEY_PACKAGES, HashSet(settings.selectedPackages))
            .putBoolean(KEY_SCANNED, settings.hasScanned)
            .putString(KEY_SCAN_MODE, settings.scanMode)
            .putString(KEY_FOLDER, settings.folderTreeUri)
            .putString(KEY_LOG, settings.playLog.joinToString(","))
            .putString(KEY_LAST, settings.lastPlayedUri)
            .putBoolean(KEY_INTRO, settings.introSeen)
            .putBoolean(KEY_CALLER, settings.callerVideoEnabled)
            .putString(KEY_CALLER_MODE, settings.callerVideoMode)
            .putString(KEY_CALLER_URI, settings.callerVideoUri)
            .putBoolean(KEY_CALLER_SOUND, settings.callerSound)
            .apply()
    }

    fun update(block: (AppSettings) -> AppSettings): AppSettings {
        val next = block(load())
        save(next)
        return next
    }

    fun resetSettingsKeepLibrary() {
        val current = load()
        save(
            AppSettings(
                hasScanned = current.hasScanned,
                scanMode = current.scanMode,
                folderTreeUri = current.folderTreeUri,
                introSeen = true,
            ),
        )
    }

    private fun parseLog(raw: String): List<Long> {
        if (raw.isBlank()) return emptyList()
        return raw.split(',').mapNotNull { it.toLongOrNull() }
    }

    companion object {
        private const val PREFS = "tsuki_settings"
        private const val KEY_START = "start"
        private const val KEY_END = "end"
        private const val KEY_PER_HOUR = "per_hour"
        private const val KEY_PAUSE = "pause_until"
        private const val KEY_MIN_MS = "min_ms"
        private const val KEY_MAX_MS = "max_ms"
        private const val KEY_SOUND = "sound"
        private const val KEY_SKIP = "skip"
        private const val KEY_HOME = "home"
        private const val KEY_MODE = "mode"
        private const val KEY_PACKAGES = "packages"
        private const val KEY_SCANNED = "scanned"
        private const val KEY_SCAN_MODE = "scan_mode"
        private const val KEY_FOLDER = "folder"
        private const val KEY_LOG = "log"
        private const val KEY_LAST = "last"
        private const val KEY_INTRO = "intro"
        private const val KEY_CALLER = "caller"
        private const val KEY_CALLER_MODE = "caller_mode"
        private const val KEY_CALLER_URI = "caller_uri"
        private const val KEY_CALLER_SOUND = "caller_sound"

        fun packagesToJson(packages: Set<String>): String = JSONArray(packages.toList()).toString()
    }
}
